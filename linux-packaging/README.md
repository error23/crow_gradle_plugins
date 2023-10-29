# Linux Packaging plugin

Plugin used to create packages for different linux distributions.

## Usage

* Create new gradle module for linux packaging
* Apply linux-packaging plugin in your packaging module
* Configure linux-packaging plugin extension to your needs
* Run linuxPackagingInitTask to initialize project structure
* Add dockerfiles and other resources needed to build packages in packaging module
* Set up your artifact dependencies in build.gradle see example below
* Run packageLinux task to build packages

## Example how to apply plugin in packaging module

```packaging/build.gradle```

```groovy
plugins {
	id 'com.crow.gradle.plugins.linux-packaging' version '1.1.0'
}
```

## Example how to add artifact dependencies

Let's say you have the following structure :

```
--- root
    |--- build.gradle
    |--- server/
         |--- build.gradle
         |--- src/main/java
         |--- build/libs/server.jar (executable jar artifact produced by server module)
    |--- packaging/
         |--- build.gradle
         |--- src/main/linux
              |--- Debian/
                   |--- Docker/
                        |--- Dockerfile (dockerfile that will build debian package)
                   |--- src/ (sources that will be packaged in debian package)
                        |--- usr/
                             |--- share/
                                  |--- server/
                                       |--- server.sh
                                       |--- server.bat
```

* First thing to do is to add dependency ```distributionArtifacts``` to your packaging module referencing your server module artifact configuration for example archives produced by java plugin

```packaging/build.gradle```

```groovy
dependencies {
	distributionArtifacts project(path: ":server", configuration: "archives")
}
```

* Next thing to do is to configure ```processArtifactsTask``` task to process your artifacts
  for example if you want to copy your server.jar to ```usr/bin/servere.jar``` in your debian package you can do it like this :

```packaging/build.gradle```

```groovy
linuxPackagingConfig {
	processArtifactsTask {
		distributionPath = "usr/bin"
	}
}
```

:::note
See processArtifactsTask extension for finer tuning.
:::

### Advanced usage :

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
