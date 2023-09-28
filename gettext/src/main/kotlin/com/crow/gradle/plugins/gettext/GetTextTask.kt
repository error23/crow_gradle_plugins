package com.crow.gradle.plugins.gettext

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

/**
 * Extracts translatable strings using xgettext from source files and
 * generates a POT file.
 */
abstract class GetTextTask : BaseGetTextTask() {

	/** Set of -k keywords to be used to extract translatable strings. */
	@get:Input
	abstract val keywords: SetProperty<String>

	/** Set of source files to extract translatable strings from. */
	@get:InputFiles
	@get:SkipWhenEmpty
	@get:IgnoreEmptyDirectories
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val sourceFiles: ConfigurableFileCollection

	/** Generated .pot file with extracted translatable strings. */
	@get:OutputFile
	abstract val potFile: RegularFileProperty

	/** Executes getText command and generates pot file from source files. */
	@TaskAction
	fun execute() {

		// Create parent directories for pot file if they don't exist
		if (!potFile.get().asFile.parentFile.exists()) potFile.get().asFile.parentFile.mkdirs()

		// Create temporary directory for inputFilesList.txt if it doesn't exist
		val tempI18nBuildDir = project.layout.buildDirectory.dir("i18n")
		if (!tempI18nBuildDir.get().asFile.exists()) tempI18nBuildDir.get().asFile.mkdirs()

		// Write all source files paths to inputFilesList.txt
		val inputFilesList = tempI18nBuildDir.get().file("inputFilesList.txt")
		inputFilesList.asFile
		  .writer(charset(encoding.get())).buffered().use { writer ->
			  sourceFiles.forEach { file ->
				  val path = file.relativeTo(project.projectDir).path
				  logger.info("Adding file : {} to inputFilesList.txt", path)
				  writer.append(path).append(System.lineSeparator())
			  }
		  }

		// Execute gettext command using inputFilesList.txt
		project.exec {
			executable = cmd.get()
			args(executableArgs.get().toList())
			if (logger.isInfoEnabled) args("--verbose")
			args("--from-code=${encoding.get()}")
			args("--keyword=${keywords.get().joinToString(separator = " -k")}".split(" "))
			args("--files-from=${inputFilesList.asFile.relativeTo(project.projectDir).path}")
			args("-o${potFile.get().asFile.relativeTo(project.projectDir).path}")

		}
		if (potFile.get().asFile.exists()) potFile.get().asFile.setEncoding(encoding.get())
	}
}
