package com.crow.gradle.plugins.gettext

import java.io.File
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

/** GetText plugin base extension. */
open class GetTextBaseExtension @Inject constructor(objects: ObjectFactory) {

	/** Task description. */
	val description = objects.property<String>()

	/** Command to execute. */
	val cmd = objects.property<String>()

	/** Command extra arguments. */
	val executableArgs = objects.setProperty<String>()

	/** File encoding. */
	val encoding = objects.property<String>().convention("UTF-8")

}

/** GetTextTask extension used to configure [GetTextTask] task. */
open class GetTextGetTextTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: GetTextBaseExtension(objects) {

	/** Set of -k keywords to be used to extract translatable strings. */
	val keywords = objects.setProperty<String>().convention(setOf(
	  "trc:lc,2",
	  "trnc:lc,2,3",
	  "tr",
	  "mrktr",
	  "trn:1,2"
	))

	/** Set of source files to extract translatable strings from. */
	val sourceFiles = objects.fileCollection().from(project.layout.projectDirectory.dir("src/main/java").asFileTree.matching { include("**/*.java") })

	/**
	 * Override of general plugin setting for generated .pot file with
	 * extracted translatable strings.
	 */
	val potFile = objects.fileProperty().convention(project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot"))

	init {
		description.convention("Extracts translatable strings using xgettext from source files and generates a POT file.")
		cmd.convention("xgettext")
		executableArgs.convention(setOf(
		  "--package-name=${project.group}.${project.name}",
		  "--package-version=${project.version}",
		  "-LJava",
		  "-n",
		  "--no-wrap",
		  "-F",
		  "--msgid-bugs-address=${project.properties.getOrDefault("developers", "developers")}",
		  "-k"
		))
	}
}

/** GetTextTask extension used to configure [MsgMergeTask] task. */
open class GetTextMsgMergeTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: GetTextBaseExtension(objects) {

	/**
	 * Override of general plugin setting for generated .pot file with
	 * extracted translatable strings.
	 */
	val potFile = objects.fileProperty().convention(project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot"))

	/** Override of general plugin i18n Directory containing lc.po files. */
	val i18nDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources/i18n"))

	init {
		description.convention("Merge translatable strings using msgmerge from keys.pot to lc.po files.")
		cmd.convention("msgmerge")
		executableArgs.convention(setOf(
		  "--no-wrap",
		  "-F",
		  "-q"
		))
	}
}

/** GetTextTask extension used to configure [MsgFmtTask] task. */
open class GetTextMsgFmtTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: GetTextBaseExtension(objects) {

	/** Set of po files to generate java bundle from. */
	val poFiles = objects.fileCollection().from(project.layout.projectDirectory.dir("src/main/resources/i18n").asFileTree.matching { include("**/*.po") })

	/** Output directory for generated java bundle. */
	val outputDirectory = objects.directoryProperty().convention(project.layout.buildDirectory.dir("classes/java/main"))

	/** Override of general plugin target bundle setting. */
	val targetBundle = objects.property<String>().convention(project.group.toString() + "." + project.name + ".i18n.Messages")

	/** If true, check if all strings are translated and fail if not. */
	val checkTranslated: Property<Boolean> = objects.property<Boolean>().convention(true)

	init {
		description.convention("Generate java bundle resources and classes from po files.")
		cmd.convention("msgfmt")
		executableArgs.convention(setOf(
		  "--java2",
		  "-c",
		  "-f"
		))
	}
}

/** GetTextTask extension used to configure [GetTextPropertyTask]. */
open class GetTextPropertyTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: GetTextBaseExtension(objects) {

	/** Override of general plugin target bundle setting. */
	val targetBundle = objects.property<String>().convention(project.group.toString() + "." + project.name + ".i18n.Messages")

	/** Generated properties configuration file. */
	val i18nPropertiesFile = objects.fileProperty().convention(project.layout.buildDirectory.file("resources/main/i18n.properties"))

	/** Generated default translation properties file. */
	val defaultTranslationPropertyFile = objects.fileProperty().convention(project.layout.buildDirectory.file(targetBundle.map { "resources/main/${it.replace('.', File.separatorChar)}.properties" }))

	init {
		description.convention("Generate properties from targetBundle for gettext-commons library.")
	}
}

/** Global GetText plugin extension. */
abstract class GetTextExtension @Inject constructor(objects: ObjectFactory, project: Project) {

	/**
	 * Global file encoding configuration, can be overridden by each task
	 * extension.
	 *
	 * Default to: ```'UTF-8'```.
	 */
	val encoding = objects.property<String>().convention("UTF-8")

	/**
	 * Global potFile configuration, can be overridden
	 * by [GetTextGetTextTaskExtension.potFile] and
	 * [GetTextMsgMergeTaskExtension.potFile] separately.
	 *
	 * Default to: ```src/main/resources/i18n/keys.pot```.
	 */
	val potFile = objects.fileProperty().convention(project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot"))

	/**
	 * Global i18nDirectory configuration, can be overridden
	 * by [GetTextMsgMergeTaskExtension.i18nDirectory] and
	 * [GetTextMsgFmtTaskExtension.poFiles] separately.
	 *
	 * Default to :
	 * ```
	 * i18nDirectory = src/main/resources/i18n
	 * poFiles = src/main/resources/i18n/**/*.po
	 * ```
	 */
	val i18nDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources/i18n"))

	/**
	 * Global targetBundle configuration, can be overridden
	 * by [GetTextMsgFmtTaskExtension.targetBundle] and
	 * [GetTextPropertyTaskExtension.targetBundle] separately.
	 *
	 * Default to: ```${project.group}.${project.name}.i18n.Messages```.
	 */
	val targetBundle = objects.property<String>().convention(project.group.toString() + "." + project.name + ".i18n.Messages")

	/** [GetTextTask] specific configuration. */
	@get:Nested
	abstract val getTextTask: GetTextGetTextTaskExtension

	/** [MsgMergeTask] specific configuration. */
	@get:Nested
	abstract val msgMergeTask: GetTextMsgMergeTaskExtension

	/** [MsgFmtTask] specific configuration. */
	@get:Nested
	abstract val msgFmtTask: GetTextMsgFmtTaskExtension

	/** [GetTextPropertyTask] specific configuration. */
	@get:Nested
	abstract val getTextPropertyTask: GetTextPropertyTaskExtension

	fun getTextTask(action: Action<in GetTextGetTextTaskExtension>) {
		action.execute(getTextTask)
	}

	fun msgMergeTask(action: Action<in GetTextMsgMergeTaskExtension>) {
		action.execute(msgMergeTask)
	}

	fun msgFmtTask(action: Action<in GetTextMsgFmtTaskExtension>) {
		action.execute(msgFmtTask)
	}

	fun getTextPropertyTask(action: Action<in GetTextPropertyTaskExtension>) {
		action.execute(getTextPropertyTask)
	}

}
