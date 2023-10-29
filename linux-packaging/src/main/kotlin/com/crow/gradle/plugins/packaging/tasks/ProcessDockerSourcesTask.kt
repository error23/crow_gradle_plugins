package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Task that processes docker sources. */
abstract class ProcessDockerSourcesTask : LinuxPackagingProcessBaseTask() {

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
	@get:InputDirectory
	abstract val distributionDirectory: DirectoryProperty

	/**
	 * Docker source directory name.
	 *
	 * @see [ProcessDockerSourcesTask.distributionDirectory]
	 */
	@get:Input
	abstract val dockerSourceDirectoryName: Property<String>

	/** Build output directory. */
	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@TaskAction
	fun execute() {
		for (packageType in packageTypes.get()) {

			project.copy {
				from(distributionDirectory.get().dir(packageType).dir(dockerSourceDirectoryName.get()).asFile)
				into(outputDirectory.get().dir(packageType).asFile)
				addReplaceTokensFilter()
				addInclude()
				addExclude()
			}
		}
	}
}
