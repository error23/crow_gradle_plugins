package com.crow.gradle.plugins.gettext

import java.io.File
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/** Merge translatable strings using msgmerge from keys.pot to lc.po files. */
abstract class MsgMergeTask : BaseGetTextTask() {

	/** keys.pot file with extracted translatable strings */
	@get:InputFile
	@get:SkipWhenEmpty
	@get:PathSensitive(PathSensitivity.NONE)
	abstract val potFile: RegularFileProperty

	/** Directory containing lc.po files. */
	@get:InputDirectory
	@get:Incremental
	@get:PathSensitive(PathSensitivity.RELATIVE)
	abstract val i18nDirectory: DirectoryProperty

	/** Generated and updated .po files with merged translatable strings. */
	@get:OutputFiles
	abstract val poFiles: ConfigurableFileCollection

	/**
	 * Executes msgmerge command and generates po files from pot file.
	 *
	 * @param inputChanges incremental changes descriptor
	 */
	@TaskAction
	fun execute(inputChanges: InputChanges) {

		val changed = getChanged(inputChanges)
		if (changed.isEmpty()) {
			logger.lifecycle("No changes detected skipping msgmerge")
			return
		}

		changed.forEach { poFile ->

			logger.info("Merging ${poFile.name} with ${potFile.get().asFile.name}")

			poFile.toLocaleString()
			if (poFile.length() == 0L) potFile.get().asFile.copyTo(poFile, true)

			project.exec {
				executable = cmd.get()
				args(executableArgs.get())
				if (logger.isInfoEnabled) args("--verbose")
				args("--update")
				args("--lang=${poFile.nameWithoutExtension}")
				args(poFile.relativeTo(project.projectDir).path)
				args(potFile.get().asFile.relativeTo(project.projectDir).path)
			}

			poFile.updateHeader(potFile.get().asFile)
			poFiles.files.add(poFile)
		}
	}

	/**
	 * Returns files that need to be updated.
	 *
	 * If pot file has changed than we need to run msgmerge on all po files.
	 *
	 * If a new po file has been added we need to run msgmerge on those files
	 * only.
	 *
	 * @param inputChanges incremental changes descriptor
	 */
	private fun getChanged(inputChanges: InputChanges): Array<File> {

		val files = mutableSetOf<File>()

		// if pot file has changed than we need to run msgmerge on all po files
		if (inputChanges.getFileChanges(potFile).any()) {
			i18nDirectory.get().asFile.listFiles { file -> file.name.endsWith(".po") }?.let { files.addAll(it) }
		}

		// if a new po file has been added we need to run msgmerge on those files only
		inputChanges.getFileChanges(i18nDirectory).forEach { change ->
			if (change.file.name.endsWith(".po") && change.changeType == ChangeType.ADDED) {
				files.add(change.file)
			}
		}

		return files.toTypedArray()
	}
}
