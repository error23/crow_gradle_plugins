# Linux Packaging plugin

Plugin used to create packages for different linux distributions.

## Usage

* Create new gradle module for linux packaging
* Apply linux-packaging plugin in your packaging module
* Configure linux-packaging plugin extension to your needs
* Run linuxPackagingInitTask to initialize project structure
* Add dockerfiles and other resources needed to build packages in packaging module
* In your dockerfile build and save your package in /root/build/artifacts directory
* Set up your artifact dependencies in build.gradle see example below
* Run packageLinux task to build packages

## Example how to apply plugin in packaging module

### - create new packaging gradle module

* In your project just create new gradle module for packaging

### - create new build.gradle in packaging module and apply linux-packaging plugin

* You have to define packageTypes that you want to build here is DISTRO it could be any linux distribution like Debian, RedHat, etc.

```packaging/build.gradle```

```groovy
plugins {
	id 'com.crow.gradle.plugins.linux-packaging' version '1.3.0'
}

linuxPackagingConfig {

	/**
	 * Global setting of different linux distributions to package.
	 *
	 * @see [ LinuxPackagingBaseExtension.packageTypes ]
	 */
	packageTypes = ["DISTRO"].toSet()
	packageName = "yourAppPackageName"
}
```

### - Run init task to initialize module structure

* This will create necessary directories and files in your module structure that is needed to build packages

* This structure can be modified with linuxPackagingInitTask extension or global extension see more in [Advanced usage](#advanced-usage)

```bash
user@host[~/project/packaging]$ ./gradlew linuxPackagingInitTask
```

### - Add artifact dependencies

* Let's say you have the following structure :

```
--- project/
    |--- build.gradle
    |--- yourApp/
         |--- build.gradle
         |--- src/main/java
         |--- build/libs/yourApp.jar (executable jar artifact produced by your app module)
    |--- packaging/ (your packaging module)
         |--- build.gradle
         |--- src/main/linux
              |--- DISTRO/
                   |--- Docker/
                        |--- Dockerfile (dockerfile that will build DISTRO package)
                   |--- src/ (sources that will be packaged in DISTRO package)
                        |--- usr/
                             |--- share/
                                  |--- yourAppSharedScripts/
                                       |--- your.sh
                                       |--- your.bat
                                       |--- your.properties
```

* First thing to do is to add dependency ```distributionArtifacts``` to your packaging module referencing your app module artifact configuration for example archives produced by java plugin

```packaging/build.gradle```

```groovy
dependencies {
	distributionArtifacts project(path: ":yourApp", configuration: "archives")
}
```

* Next thing to do is to configure ```processArtifactsTask``` task to process your artifacts for example if you want to copy your ```yourApp.jar``` to ```usr/bin/yourApp.jar``` in your DISTRO package you can do it like this :

```packaging/build.gradle```

```groovy
linuxPackagingConfig {
	processArtifactsTask {
		distributionPath = "usr/bin"
	}
}
```

* NOTE: you can also use ```copyInclude``` and ```copyExclude``` to filter files that you want to copy. See processArtifactsTask extension for finer tuning.

### - Add build sources to docker container

* By default, you should store your distro specific build resources like ```DEBIAN/control``` inside of ```DISTRO/src``` folder for example if DISTRO is debian package you should have something like ```src/main/linux/Debian/src/DEBIAN/control```
    * In our example above ```/packaging/src/main/linux/DISTRO/src/...``` is specific for DISTRO distribution and will be copied only in docker container for DISTRO distribution


* For shared ressources between your distributions for example let's say that file ```your-service.service```  should be installed in all distributions at same place ```etc/systemd/system/your-service.service``` than you can put it inside of ```/src/main/linux/src/etc/systemd/system/your-service.service```

#### Example :

* So if we take structure described above in your debian docker container you should have
    * ```/root/build/yourAppPackageName/DEBIAN/control``` copied from ```src/main/linux/Debian/src/DEBIAN/control```
    * ```/root/build/yourAppPackageName/usr/bin/yourApp.jar``` copied from ```distributionArtifacts```
    * ```/root/build/yourAppPackageName/usr/share/yourAppSharedScripts/*``` copied from ```/src/main/linux/src/usr/share/yourAppSharedScripts/*```
    * ```/root/build/yourAppPackageName/etc/systemd/system/your-service.service``` copied from ```/src/main/linux/src/etc/systemd/system/your-service.service```

* In your other containers like for example Fedora you should have
    * in ```/build/root/yourAppPackageName``` all specific packages that are in ```src/main/linux/Fedora/src/``` and no packages from ```src/main/linux/Debian/src/```
    * ```/root/build/yourAppPackageName/usr/bin/yourApp.jar``` copied from ```distributionArtifacts```
    * ```/root/build/yourAppPackageName/usr/share/yourAppSharedScripts/*``` copied from ```/src/main/linux/src/usr/share/yourAppSharedScripts/*```
    * ```/root/build/yourAppPackageName/etc/systemd/system/your-service.service``` copied from ```/src/main/linux/src/etc/systemd/system/your-service.service```

### - Add Dockerfile

* Inside every DISTRO folder it should be Docker/ folder where you can write your Dockerfile that will build your linux container, in this folder you can also find all scripts that are needed to docker container itself for example entrypoint.sh or any other ressource you might need in your docker container
* Your docker container must have at least folder ```/root/build/artifacts/``` else packageLinux task will fail

#### Example of minimal dockerfile:

```/src/main/linux/DISTRO/Docker/Dockefile```

```dockerfile
FROM DISTRO:version

RUN mkdir -p /root/build/artifacts
WORKDIR /root/build

RUN yourPackageTool --input-directory yourAppPackageName --output-directory /root/build/artifacts

```

* Best practice is to use ```ENTRYPOINT``` in order to run your package tool and export output package inside of ```/root/build/artifacts/``` folder which is shared at the end inside your project ```build``` folder so you can retrieve your built packages

### - Add build resources to docker container

* There are two ways of adding ressources to a docker container you can put them else inside of ```/src/main/linux/DISTRO/Docker/``` than simply use ```COPY``` inside that specific Dockerfile
* Or you can put them inside of ```/src/main/linux/resoureces``` and that use ```COPY``` inside Dockerfile from any DISTRO


* For example if you have DebianEntryPoint.sh you should put it in ```/src/main/linux/Debian/Docker/``` directly so it will be disponible by ```/src/main/linux/Debian/Docker/Dockerfile``` to ```COPY```
* But if you have some sharedScript.sh that works for any DISTRO you package than you should put it inside of ```/src/main/linux/resoureces/sharedScript.sh``` and it will be disponible by all Dockerfile to ```COPY```

## Advanced usage

If you need to customize any plugin settings you can use following extension
All values presented here except filterProperties copyInclude and copyExclude ones are default values set by plugin but in case you need to change some configuration here is complete description of the extension

```groovy
linuxPackagingConfig {

	/**
	 * Global setting of different linux distributions to package.
	 *
	 * @see [ LinuxPackagingBaseExtension.packageTypes ]
	 */
	packageTypes = ["Debian", "RedHat"].toSet()

	/**
	 * Global distribution sources directory name.
	 *
	 * @see [ LinuxPackagingInitTaskExtension.sourceDirectoryName ]
	 */
	sourceDirectoryName = "src"

	/**
	 * Global docker source directory name.
	 *
	 * @see [ LinuxPackagingInitTaskExtension.dockerSourceDirectoryName ]
	 */
	dockerSourceDirectoryName = "Docker"

	/**
	 * Global setting of directory containing different linux distributions
	 * sources.
	 *
	 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
	 */
	distributionDirectory = layout.projectDirectory.dir("src/main/linux")

	/**
	 * Global setting of directory containing shared resources needed to build
	 * docker images.
	 *
	 * @see [ LinuxPackagingInitTaskExtension.resourcesDirectory ]
	 */
	resourcesDirectory = layout.projectDirectory.dir("src/main/resources")

	/**
	 * Global setting of directory containing common sources for all
	 * distributions.
	 *
	 * @see [ LinuxPackagingInitTaskExtension.commonSrcDirectory ]
	 */
	commonSrcDirectory = layout.projectDirectory.dir("src/main/linux/src")

	/**
	 * Global build output directory.
	 *
	 * @see [ ProcessDockerSourcesTaskExtension.outputDirectory ]
	 */
	outputDirectory = layout.buildDirectory.dir("deployment")

	/** Overrides global distributed package name. */
	packageName = "${project.name}_${project.version}"

	/**
	 * Global properties to replace in the files while processing sources or
	 * resources.
	 *
	 * @see [ ReplaceTokens ]
	 */
	filterProperties = [
			"version": project.version,
			"prop"   : "someValue"
	]

	linuxPackagingInitTask {

		/** Task description. */
		description = "Initializes linux packaging project structure."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Overrides global settings of directory containing different linux
		 * distributions sources.
		 *
		 * By convention this directory contains [commonSrcDirectory] which is a
		 * common source directory for all distributions (packageTypes) containing
		 * your application sources that should be packaged.
		 * * Each distribution (packageType) has its own subdirectory.
		 * * Each distribution (packageType) subdirectory contains a src directory
		 *   containing your application sources that should be packaged for this
		 *   specific distribution.
		 * * Each distribution (packageType) subdirectory contains a Docker
		 *   directory containing the Dockerfile and docker related ressources
		 *   needed to build the package.
		 */
		distributionDirectory = layout.projectDirectory.dir("src/main/linux")

		/**
		 * Overrides global distribution sources directory name.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.sourceDirectoryName ]
		 */
		sourceDirectoryName = "src"

		/**
		 * Overrides global docker source directory name.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.dockerSourceDirectoryName ]
		 */
		dockerSourceDirectoryName = "Docker"

		/**
		 * Overrides global settings of directory containing shared resources
		 * needed to build docker images.
		 *
		 * This directory contains common ressources to all docker images needed
		 * to build the package. Anything in this folder will be copied along
		 * the dockerfiles for every distribution same way as if you put it in
		 * [distributionDirectory]/$packageType/Docker.
		 */
		resourcesDirectory = layout.projectDirectory.dir("src/main/resources")

		/**
		 * Overrides global settings of directory containing common sources for all
		 * distributions.
		 *
		 * This directory contains shared sources for all distributions
		 * (packageTypes) that should be packaged. Anything in this folder will be
		 * copied along the sources for every distribution same way as if you put
		 * it in [distributionDirectory]/$packageType/src.
		 */
		commonSrcDirectory = layout.projectDirectory.dir("src/main/linux/src")

	}

	processDockerSourcesTask {

		/** Task description. */
		description = "Process docker sources."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Include pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.include ]
		 */
		copyInclude = ["*"]

		/**
		 * Exclude pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.exclude ]
		 */
		copyExclude = ["*.txt"]

		/**
		 * Overrides global properties to replace in the files.
		 * By default this value is not set and no properties are replaced.
		 *
		 * @see [ ReplaceTokens ]
		 */
		filterProperties = [
				"version": project.version,
				"prop"   : "someValue"
		]

		/**
		 * Overrides global settings of directory containing different linux
		 * distributions sources.
		 *
		 * By convention this directory contains
		 * [LinuxPackagingInitTaskExtension.commonSrcDirectory] which is a common
		 * source directory for all distributions (packageTypes) containing your
		 * application sources that should be packaged.
		 * * Each distribution (packageType) has its own subdirectory.
		 * * Each distribution (packageType) subdirectory contains a src directory
		 *   containing your application sources that should be packaged for this
		 *   specific distribution.
		 * * Each distribution (packageType) subdirectory contains a Docker
		 *   directory containing the Dockerfile and docker related ressources
		 *   needed to build the package.
		 */
		distributionDirectory = layout.projectDirectory.dir("src/main/linux")

		/**
		 * Overrides global docker source directory name.
		 *
		 * @see [ ProcessDockerSourcesTaskExtension.dockerSourceDirectoryName ]
		 */
		dockerSourceDirectoryName = "Docker"

		/**
		 * Overrides global build output directory.
		 */
		outputDirectory = layout.buildDirectory.dir("deployment")

	}

	processDistributionSourcesTask {

		/** Task description. */
		description = "Process distribution sources."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Include pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.include ]
		 */
		copyInclude = ["*"]

		/**
		 * Exclude pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.exclude ]
		 */
		copyExclude = ["*.txt"]

		/**
		 * Overrides global properties to replace in the files.
		 * By default this value is not set and no properties are replaced.
		 *
		 * @see [ ReplaceTokens ]
		 */
		filterProperties = [
				"version": project.version,
				"prop"   : "someValue"
		]

		/**
		 * Overrides global settings of directory containing different linux
		 * distributions sources.
		 *
		 * By convention this directory contains
		 * [LinuxPackagingInitTaskExtension.commonSrcDirectory] which is a common
		 * source directory for all distributions (packageTypes) containing your
		 * application sources that should be packaged.
		 * * Each distribution (packageType) has its own subdirectory.
		 * * Each distribution (packageType) subdirectory contains a src directory
		 *   containing your application sources that should be packaged for this
		 *   specific distribution.
		 * * Each distribution (packageType) subdirectory contains a Docker
		 *   directory containing the Dockerfile and docker related ressources
		 *   needed to build the package.
		 */
		distributionDirectory = layout.projectDirectory.dir("src/main/linux")

		/**
		 * Overrides global distribution sources directory name.
		 *
		 * @see [ ProcessDistributionSourcesTaskExtension.sourceDirectoryName ]
		 */
		sourceDirectoryName = "src"

		/**
		 * Overrides global build output directory.
		 */
		outputDirectory = layout.buildDirectory.dir("deployment")

		/** Overrides global distributed package name. */
		packageName = "${project.name}_${project.version}"
	}

	processSharedSourcesTask {

		/** Task description. */
		description = "Process distribution shared sources."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Include pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.include ]
		 */
		copyInclude = ["*"]

		/**
		 * Exclude pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.exclude ]
		 */
		copyExclude = ["*.txt"]

		/**
		 * Overrides global properties to replace in the files.
		 * By default this value is not set and no properties are replaced.
		 *
		 * @see [ ReplaceTokens ]
		 */
		filterProperties = [
				"version": project.version,
				"prop"   : "someValue"
		]

		/**
		 * Overrides global settings of directory containing common sources for all
		 * distributions.
		 *
		 * This directory contains shared sources for all distributions
		 * (packageTypes) that should be packaged. Anything in this folder will be
		 * copied along the sources for every distribution same way as if you put
		 * it in [LinuxPackagingInitTask.distributionDirectory]/$packageType/src.
		 */
		commonSrcDirectory = layout.projectDirectory.dir("src/main/linux/src")

		/**
		 * Overrides global build output directory.
		 */
		outputDirectory = layout.buildDirectory.dir("deployment")

		/** Overrides global distributed package name. */
		packageName = "${project.name}_${project.version}"
	}

	processSharedResourcesTask {

		/** Task description. */
		description = "Process shared resources."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Include pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.include ]
		 */
		copyInclude = ["*"]

		/**
		 * Exclude pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.exclude ]
		 */
		copyExclude = ["*.txt"]

		/**
		 * Overrides global properties to replace in the files.
		 * By default this value is not set and no properties are replaced.
		 *
		 * @see [ ReplaceTokens ]
		 */
		filterProperties = [
				"version": project.version,
				"prop"   : "someValue"
		]

		/**
		 * Overrides global settings of directory containing common sources for all
		 * distributions.
		 *
		 * This directory contains shared sources for all distributions
		 * (packageTypes) that should be packaged. Anything in this folder will be
		 * copied along the sources for every distribution same way as if you put
		 * it in [LinuxPackagingInitTask.distributionDirectory]/$packageType/src.
		 */
		commonSrcDirectory = layout.projectDirectory.dir("src/main/linux/src")

		/**
		 * Overrides global build output directory.
		 */
		outputDirectory = layout.buildDirectory.dir("deployment")

	}

	processArtifactsTask {

		/** Task description. */
		description = "Process shared resources."

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Include pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.include ]
		 */
		copyInclude = ["server/**/**"]

		/**
		 * Exclude pattern for the copy task.
		 * By default this value is not set and all files are copied.
		 * @see [ CopySpec.exclude ]
		 */
		copyExclude = ["**/server.bat"]

		/**
		 * Overrides global properties to replace in the files.
		 * By default this value is not set and no properties are replaced.
		 *
		 * @see [ ReplaceTokens ]
		 */
		filterProperties = [
				"version": project.version,
				"prop"   : "someValue"
		]

		/**
		 * Overrides global build output directory.
		 */
		outputDirectory = layout.buildDirectory.dir("deployment")

		/**
		 * Overrides global distributed package name.
		 */
		packageName = "${project.name}_${project.version}"

		/**
		 * Artifacts to process.
		 */
		artifacts = configurations.distributionArtifacts

		/*
		 *  Path of the artifact inside of [outputDirectory]/[packageName].
		 */
		distributionPath = "usr/share/${project.name}"

		/*
		 * If true unzip untar the artifact into the [distributionPath] directory.
		 */
		unArchive = true

	}

	buildDockerImageTask {

		/**
		 * Overrides global distributed package name.
		 */
		packageName = "${project.name}_${project.version}"

		/**
		 * Overrides global settings of different linux distributions to package.
		 *
		 * @see [ LinuxPackagingInitTaskExtension.distributionDirectory ]
		 */
		packageTypes = ["Debian", "RedHat"].toSet()

		/**
		 * Docker input directory.
		 *
		 * containing [packageTypes] subdirectories containing the docker files
		 */
		inputDirectory = layout.buildDirectory.dir("deployment")

		/**
		 * Prefix of docker image name to build.
		 *
		 * Full docker image name will be $dockerImageNamePrefix$packageType.
		 */
		dockerImageNamePrefix = "server"
	}
}
```
