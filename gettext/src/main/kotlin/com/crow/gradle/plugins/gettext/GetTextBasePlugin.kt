package com.crow.gradle.plugins.gettext

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/** Base class for gettext plugins. */
open class GetTextBasePlugin : Plugin<Project> {

	private val taskGroup = "i18n"

	override fun apply(project: Project) {

		// Create gettext extension
		val extension = project.extensions.create<GetTextExtension>("getTextConfig")

		// Set encoding convention
		extension.getTextTask.encoding.convention(extension.encoding)
		extension.msgMergeTask.encoding.convention(extension.encoding)
		extension.msgFmtTask.encoding.convention(extension.encoding)
		extension.getTextPropertyTask.encoding.convention(extension.encoding)

		// Set pot file convention
		extension.getTextTask.potFile.convention(extension.potFile)
		extension.msgMergeTask.potFile.convention(extension.potFile)

		// Set i18n directory convention
		extension.msgMergeTask.i18nDirectory.convention(extension.i18nDirectory)
		extension.msgFmtTask.poFiles.setFrom(extension.i18nDirectory.map { it.asFileTree.matching { include("**/*.po") } })

		// Set target bundle convention
		extension.msgFmtTask.targetBundle.convention(extension.targetBundle)
		extension.getTextPropertyTask.targetBundle.convention(extension.targetBundle)

		// Register tasks
		project.tasks.register<GetTextTask>("getText") {
			group = taskGroup
			description = extension.getTextTask.description.get()
			cmd.set(extension.getTextTask.cmd)
			executableArgs.set(extension.getTextTask.executableArgs)
			encoding.set(extension.getTextTask.encoding)
			keywords.set(extension.getTextTask.keywords)
			sourceFiles.from(extension.getTextTask.sourceFiles)
			potFile.set(extension.getTextTask.potFile)
		}

		project.tasks.register<MsgMergeTask>("msgMerge") {
			group = taskGroup
			description = extension.msgMergeTask.description.get()
			cmd.set(extension.msgMergeTask.cmd)
			executableArgs.set(extension.msgMergeTask.executableArgs)
			encoding.set(extension.msgMergeTask.encoding)
			potFile.set(extension.msgMergeTask.potFile)
			i18nDirectory.set(extension.msgMergeTask.i18nDirectory)
		}

		project.tasks.register<MsgFmtTask>("msgFmt") {
			group = taskGroup
			description = extension.msgFmtTask.description.get()
			cmd.set(extension.msgFmtTask.cmd)
			executableArgs.set(extension.msgFmtTask.executableArgs)
			encoding.set(extension.msgFmtTask.encoding)
			poFiles.setFrom(extension.msgFmtTask.poFiles)
			targetBundle.set(extension.msgFmtTask.targetBundle)
			outputDirectory.set(extension.msgFmtTask.outputDirectory)
			checkTranslated.set(extension.msgFmtTask.checkTranslated)
		}

		project.tasks.register<GetTextPropertyTask>("generateI18nProperties") {
			group = taskGroup
			description = extension.getTextPropertyTask.description.get()
			encoding.set(extension.getTextPropertyTask.encoding)
			targetBundle.set(extension.getTextPropertyTask.targetBundle)
			i18nPropertiesFile.set(extension.getTextPropertyTask.i18nPropertiesFile)
			defaultTranslationPropertyFile.set(extension.getTextPropertyTask.defaultTranslationPropertyFile)
		}
	}
}
