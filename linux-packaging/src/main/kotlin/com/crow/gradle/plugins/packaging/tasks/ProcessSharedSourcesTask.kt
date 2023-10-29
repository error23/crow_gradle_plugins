package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Task that processes shared sources. */
abstract class ProcessSharedSourcesTask : LinuxPackagingProcessBaseTask() {

	/**
	 * Directory containing common sources for all distributions.
	 *
	 * This directory contains shared sources for all distributions
	 * (packageTypes) that should be packaged. Anything in this folder will be
	 * copied along the sources for every distribution same way as if you put
	 * it in [LinuxPackagingInitTask.distributionDirectory]/$packageType/src.
	 */
	@get:InputDirectory
	abstract val commonSrcDirectory: DirectoryProperty

	/** Build output directory. */
	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	/** Distributed package name. */
	@get:Input
	abstract val packageName: Property<String>

	@TaskAction
	fun execute() {

		for (packageType in packageTypes.get()) {
			project.copy {
				from(commonSrcDirectory.get().asFile)
				into(outputDirectory.get().dir(packageType).dir(packageName.get()).asFile)
				addReplaceTokensFilter()
				addInclude()
				addExclude()
				duplicatesStrategy = DuplicatesStrategy.EXCLUDE
			}
		}
	}
}
