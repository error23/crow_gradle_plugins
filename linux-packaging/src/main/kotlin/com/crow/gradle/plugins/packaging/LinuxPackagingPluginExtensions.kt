import javax.inject.Inject
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

/** Linux packaging plugin base extension. */
open class LinuxPackagingBaseExtension @Inject constructor(objects: ObjectFactory) {

	/** Task description. */
	val description = objects.property<String>()

	/**
	 * Overrides global settings of different linux distributions to package.
	 *
	 * @see [LinuxPackagingInitTaskExtension.distributionDirectory]
	 */
	val packageTypes = objects.setProperty<String>().convention(setOf(
	  "Debian",
	  "RedHat"
	))
}

open class LinuxPackagingProcessBaseExtension @Inject constructor(objects: ObjectFactory) : LinuxPackagingBaseExtension(objects) {

	/**
	 * Include pattern for the copy task.
	 *
	 * @see [CopySpec.include]
	 */
	val copyInclude = objects.setProperty<String>()

	/**
	 * Exclude pattern for the copy task.
	 *
	 * @see [CopySpec.exclude]
	 */
	val copyExclude = objects.setProperty<String>()

	/**
	 * Overrides global properties to replace in the files.
	 *
	 * @see [ReplaceTokens]
	 */
	val filterProperties = objects.mapProperty<String, Any>()

}

/** Linux packaging plugin init extension. */
open class LinuxPackagingInitTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingBaseExtension(objects) {

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
	val distributionDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux"))

	/**
	 * Overrides global distribution sources directory name.
	 *
	 * @see [LinuxPackagingInitTaskExtension.distributionDirectory]
	 */
	val sourceDirectoryName = objects.property<String>().convention("src")

	/**
	 * Overrides global distribution sources directory name.
	 *
	 * @see [LinuxPackagingInitTaskExtension.distributionDirectory]
	 */
	val dockerSourceDirectoryName = objects.property<String>().convention("Docker")

	/**
	 * Overrides global settings of directory containing shared resources
	 * needed to build docker images.
	 *
	 * This directory contains common ressources to all docker images needed
	 * to build the package. Anything in this folder will be copied along
	 * the dockerfiles for every distribution same way as if you put it in
	 * [distributionDirectory]/$packageType/Docker.
	 */
	val resourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Overrides global settings of directory containing common sources for all
	 * distributions.
	 *
	 * This directory contains shared sources for all distributions
	 * (packageTypes) that should be packaged. Anything in this folder will be
	 * copied along the sources for every distribution same way as if you put
	 * it in [distributionDirectory]/$packageType/src.
	 */
	val commonSrcDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux/src"))

	init {
		description.convention("Initializes linux packaging project structure.")
	}
}

/** Linux packaging plugin process docker sources extension. */
open class ProcessDockerSourcesTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingProcessBaseExtension(objects) {

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
	val distributionDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux"))

	/**
	 * Overrides global distribution sources directory name.
	 *
	 * @see [ProcessDockerSourcesTaskExtension.distributionDirectory]
	 */
	val dockerSourceDirectoryName = objects.property<String>().convention("Docker")

	/** Overrides global build output directory. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	init {
		description.convention("Process docker sources.")
	}
}

/** Linux packaging plugin process distribution sources extension. */
open class ProcessDistributionSourcesTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingProcessBaseExtension(objects) {

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
	val distributionDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux"))

	/**
	 * Overrides global distribution sources directory name.
	 *
	 * @see [ProcessDistributionSourcesTaskExtension.distributionDirectory]
	 */
	val sourceDirectoryName = objects.property<String>().convention("src")

	/** Overrides global build output directory. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	/** Overrides distributed package name. */
	val packageName = objects.property<String>().convention("${project.name}_${project.version}")

	init {
		description.convention("Process distribution sources.")
	}
}

/** Linux packaging plugin process sources extension. */
open class ProcessSharedSourcesTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingProcessBaseExtension(objects) {

	/**
	 * Overrides global settings of directory containing common sources for all
	 * distributions.
	 *
	 * This directory contains shared sources for all distributions
	 * (packageTypes) that should be packaged. Anything in this folder will be
	 * copied along the sources for every distribution same way as if you put
	 * it in
	 * [com.crow.gradle.plugins.packaging.tasks.LinuxPackagingInitTask.distributionDirectory]/$packageType/src.
	 */
	val commonSrcDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux/src"))

	/** Overrides global build output directory. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	/** Overrides distributed package name. */
	val packageName = objects.property<String>().convention("${project.name}_${project.version}")

	init {
		description.convention("Process distribution shared sources.")
	}
}

/** Linux packaging plugin process resources extension. */
open class ProcessSharedResourcesTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingProcessBaseExtension(objects) {

	/**
	 * Overrides global settings of directory containing shared resources
	 * needed to build docker images.
	 *
	 * This directory contains common ressources to all docker images needed
	 * to build the package. Anything in this folder will be copied along
	 * the dockerfiles for every distribution same way as if you put it in
	 * [LinuxPackagingExtension.distributionDirectory]/$packageType/Docker.
	 */
	val resourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/** Overrides global build output directory. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	init {
		description.convention("Process distribution shared sources.")
	}
}

/** Linux packaging plugin process artifacts extension. */
open class ProcessArtifactsTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingProcessBaseExtension(objects) {

	/** Overrides global build output directory. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	/** Overrides distributed package name. */
	val packageName = objects.property<String>().convention("${project.name}_${project.version}")

	/** Artifacts to process. */
	val artifacts = objects.fileCollection().from(project.configurations.getByName("distributionArtifacts"))

	/** Path of the artifact inside of [outputDirectory]/[packageName]. */
	val distributionPath = objects.property<String>().convention("usr/share/${project.name}")

	/** If true unzip untar the artifact into the [distributionPath] directory. */
	val unArchive = objects.property<Boolean>().convention(false)

	init {
		description.convention("Process artifacts for linux packaging.")
	}

}

open class BuildDockerImageTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: LinuxPackagingBaseExtension(objects) {

	/** Overrides distributed package name. */
	val packageName = objects.property<String>().convention("${project.name}_${project.version}")

	/**
	 * Docker input directory.
	 *
	 * containing [packageTypes] subdirectories containing the docker files
	 */
	val inputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	/**
	 * Prefix of docker image name to build.
	 *
	 * Full docker image name will be $dockerImageNamePrefix$packageType.
	 */
	val dockerImageNamePrefix = objects.property<String>().convention(project.name)

	init {
		description.convention("Builds docker images.")
	}
}

/** Linux packaging plugin extension. */
@Suppress("Unused")
abstract class LinuxPackagingExtension @Inject constructor(objects: ObjectFactory, project: Project) {

	/**
	 * Global setting of different linux distributions to package.
	 *
	 * @see [LinuxPackagingInitTaskExtension.packageTypes]
	 */
	val packageTypes = objects.setProperty<String>().convention(setOf(
	  "Debian",
	  "RedHat"
	))

	/**
	 * Global distribution sources directory name.
	 *
	 * @see [LinuxPackagingInitTaskExtension.sourceDirectoryName]
	 */
	val sourceDirectoryName = objects.property<String>().convention("src")

	/**
	 * Global docker source directory name.
	 *
	 * @see [LinuxPackagingInitTaskExtension.dockerSourceDirectoryName]
	 */
	val dockerSourceDirectoryName = objects.property<String>().convention("Docker")

	/**
	 * Global setting of directory containing different linux distributions
	 * sources.
	 *
	 * @see [LinuxPackagingInitTaskExtension.distributionDirectory]
	 */
	val distributionDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux"))

	/**
	 * Global setting of directory containing shared resources needed to build
	 * docker images.
	 *
	 * @see [LinuxPackagingInitTaskExtension.resourcesDirectory]
	 */
	val resourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Global setting of directory containing common sources for all
	 * distributions.
	 *
	 * @see [LinuxPackagingInitTaskExtension.commonSrcDirectory]
	 */
	val commonSrcDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/linux/src"))

	/**
	 * Global build output directory.
	 *
	 * @see [ProcessSharedSourcesTaskExtension.outputDirectory]
	 */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("deployment"))

	/**
	 * Global distributed package name.
	 *
	 * @see [ProcessSharedSourcesTaskExtension.packageName]
	 */
	val packageName = objects.property<String>().convention("${project.name}_${project.version}")

	/**
	 * Global properties to replace in the files while processing sources or
	 * resources.
	 *
	 * @see [ReplaceTokens]
	 */
	val filterProperties = objects.mapProperty<String, Any>()

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.LinuxPackagingInitTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val linuxPackagingInitTask: LinuxPackagingInitTaskExtension

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.ProcessDockerSourcesTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val processDockerSourcesTask: ProcessDockerSourcesTaskExtension

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.ProcessDistributionSourcesTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val processDistributionSourcesTask: ProcessDistributionSourcesTaskExtension

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.ProcessSharedSourcesTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val processSharedSourcesTask: ProcessSharedSourcesTaskExtension

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.ProcessResourcesTask] specific
	 * configuration.
	 */
	@get:Nested
	abstract val processSharedResourcesTask: ProcessSharedResourcesTaskExtension

	/**
	 * [com.crow.gradle.plugins.packaging.tasks.ProcessArtifactsTask] specific
	 * configuration.
	 */
	@get:Nested
	abstract val processArtifactsTask: ProcessArtifactsTaskExtension

	@get:Nested
	abstract val buildDockerImageTask: BuildDockerImageTaskExtension

	fun linuxPackagingInitTask(action: Action<in LinuxPackagingInitTaskExtension>) {
		action.execute(linuxPackagingInitTask)
	}

	fun processDockerSourcesTask(action: Action<in ProcessDockerSourcesTaskExtension>) {
		action.execute(processDockerSourcesTask)
	}

	fun processDistributionSourcesTask(action: Action<in ProcessDistributionSourcesTaskExtension>) {
		action.execute(processDistributionSourcesTask)
	}

	fun processSharedSourcesTask(action: Action<in ProcessSharedSourcesTaskExtension>) {
		action.execute(processSharedSourcesTask)
	}

	fun processSharedResourcesTask(action: Action<in ProcessSharedResourcesTaskExtension>) {
		action.execute(processSharedResourcesTask)
	}

	fun processArtifactsTask(action: Action<in ProcessArtifactsTaskExtension>) {
		action.execute(processArtifactsTask)
	}

	fun buildDockerImageTask(action: Action<in BuildDockerImageTaskExtension>) {
		action.execute(buildDockerImageTask)
	}
}
