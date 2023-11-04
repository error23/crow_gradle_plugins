package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/** Task that processes shared resources. */
abstract class ProcessResourcesTask : LinuxPackagingProcessBaseTask() {

	/**
	 * Directory containing shared resources needed to build docker images.
	 *
	 * This directory contains common ressources to all docker images needed
	 * to build the package. Anything in this folder will be copied along
	 * the dockerfiles for every distribution same way as if you put it in
	 * [LinuxPackagingInitTask.distributionDirectory]/$packageType/Docker.
	 */
	@get:InputDirectory
	@get:SkipWhenEmpty
	abstract val resourcesDirectory: DirectoryProperty

	/** Build output directory. */
	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	@TaskAction
	fun execute() {
		for (packageType in packageTypes.get()) {

			project.copy {
				from(resourcesDirectory.get().asFile)
				into(outputDirectory.get().dir(packageType).asFile)
				addReplaceTokensFilter()
				addInclude()
				addExclude()
				duplicatesStrategy = DuplicatesStrategy.EXCLUDE
			}
		}
	}
}
