package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/**
 * Task that runs poetry tests.
 */
abstract class PoetryTestTask : PoetryBaseTask() {

	/**
	 * Python test command.
	 */
	@get:Input
	abstract val testCmd: Property<String>

	/**
	 *  Test command extra arguments.
	 */
	@get:[Input Optional]
	abstract val testCmdExtraArgs: SetProperty<String>

	/**
	 * Source files to track changements.
	 */
	@get:InputFiles
	@get:SkipWhenEmpty
	@get:IgnoreEmptyDirectories
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val sourceFiles: ConfigurableFileCollection

	/**
	 * Test report argument option to generate test report.
	 */
	@get:Input
	abstract val testReportArgument: Property<String>

	/**
	 * Test report output file.
	 */
	@get:OutputFile
	abstract val testReport: RegularFileProperty

	@TaskAction
	fun execute() {

		runPoetry(setOf("update", "--sync", "--with", "test"))

		val args = mutableSetOf("run", testCmd.get(), testReportArgument.get() + testReport.get().asFile.absolutePath)
		if (testCmdExtraArgs.isPresent) args += testCmdExtraArgs.get()
		runPoetry(args)
	}

}
