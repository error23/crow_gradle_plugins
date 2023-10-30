package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class ProcessArtifactsTask : LinuxPackagingProcessBaseTask() {

	/** Build output directory. */
	@get:OutputDirectory
	abstract val outputDirectory: DirectoryProperty

	/** Distributed package name. */
	@get:Input
	abstract val packageName: Property<String>

	/** Artifacts to process. */
	@get:InputFiles
	abstract val artifacts: ConfigurableFileCollection

	/** Path of the artifact inside of [outputDirectory]/[packageName]. */
	@get:Input
	abstract val distributionPath: Property<String>

	/** If true unzip untar the artifact into the [distributionPath] directory. */
	@get:Input
	abstract val unArchive: Property<Boolean>

	@TaskAction
	fun execute() {
		for (packageType in packageTypes.get()) {
			for (artifact in artifacts) {
				project.copy {
					from(if (unArchive.get()) artifact.unArchive() else artifact)
					into(outputDirectory.get().dir(packageType).dir(packageName.get()).dir(distributionPath.get()))
					addReplaceTokensFilter()
					addInclude()
					addExclude()
				}
			}
		}
	}
}
