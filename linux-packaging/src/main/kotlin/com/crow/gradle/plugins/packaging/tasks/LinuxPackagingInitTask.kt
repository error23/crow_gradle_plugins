package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Task that initializes linux packaging structure. */
abstract class LinuxPackagingInitTask : LinuxPackagingBaseTask() {

	/**
	 * Directory containing different linux distributions sources.
	 *
	 * By convention this directory contains
	 * [LinuxPackagingInitTask.commonSrcDirectory] which is a common source
	 * directory for all distributions (packageTypes) containing your
	 * application sources that should be packaged.
	 * * Each distribution (packageType) has its own subdirectory.
	 * * Each distribution (packageType) subdirectory contains a src directory
	 *   containing your application sources that should be packaged for this
	 *   specific distribution.
	 * * Each distribution (packageType) subdirectory contains a Docker
	 *   directory containing the Dockerfile and docker related ressources
	 *   needed to build the package.
	 */
	@get:OutputDirectory
	abstract val distributionDirectory: DirectoryProperty

	/**
	 * Distribution sources directory name.
	 *
	 * @see [LinuxPackagingInitTask.distributionDirectory]
	 */
	@get:Input
	abstract val sourceDirectoryName: Property<String>

	/**
	 * Docker source directory name.
	 *
	 * @see [LinuxPackagingInitTask.distributionDirectory]
	 */
	@get:Input
	abstract val dockerSourceDirectoryName: Property<String>

	/**
	 * Directory containing shared resources needed to build docker images.
	 *
	 * This directory contains common ressources to all docker images needed
	 * to build the package. Anything in this folder will be copied along
	 * the dockerfiles for every distribution same way as if you put it in
	 * [distributionDirectory]/$packageType/Docker.
	 */
	@get:OutputDirectory
	abstract val resourcesDirectory: DirectoryProperty

	/**
	 * Directory containing common sources for all distributions.
	 *
	 * This directory contains shared sources for all distributions
	 * (packageTypes) that should be packaged. Anything in this folder will be
	 * copied along the sources for every distribution same way as if you put
	 * it in [distributionDirectory]/$packageType/src.
	 */
	@get:OutputDirectory
	abstract val commonSrcDirectory: DirectoryProperty

	@TaskAction
	fun execute() {

		distributionDirectory.get().asFile.mkdirs()
		resourcesDirectory.get().asFile.mkdirs()
		commonSrcDirectory.get().asFile.mkdirs()


		for (packageType in packageTypes.get()) {
			distributionDirectory.get().dir(packageType).dir(sourceDirectoryName.get()).asFile.mkdirs()
			distributionDirectory.get().dir(packageType).dir(dockerSourceDirectoryName.get()).asFile.mkdirs()
		}

	}
}
