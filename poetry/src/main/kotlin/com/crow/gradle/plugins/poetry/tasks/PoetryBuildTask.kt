package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/**
 * Task that build python packages.
 */
abstract class PoetryBuildTask : PoetryBaseTask() {

	/**
	 *  Poetry command extra arguments.
	 */
	@get:[Input Optional]
	abstract val poetryExtraArgs: SetProperty<String>

	/**
	 * Source files to track changements.
	 */
	@get:InputFiles
	@get:SkipWhenEmpty
	@get:IgnoreEmptyDirectories
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val sourceFiles: ConfigurableFileCollection

	/**
	 * Directory containing build artifacts.
	 */
	@get:OutputDirectory
	abstract val buildDirectory: DirectoryProperty

	@TaskAction
	fun execute() {

		val args = mutableSetOf(
		  "build",
		  "--output", buildDirectory.get().asFile.absolutePath
		)

		if (poetryExtraArgs.isPresent) args += poetryExtraArgs.get()
		runPoetry(args)
	}
}
