# crow gnu gettext plugin

Get text plugin defines usual workflow to generate pot, po and javaBundle files from java code source using GetTextBase plugin

## Usage

If you are using gettext-commons library with java normally you would just need to apply plugin

```groovy
plugins {
	id 'com.crow.gradle.plugins.gettext' version '1.1.0'
}
```

### Advanced usage :

If you need to customize some plugin settings you can use following extension
All values presented here are default values set by plugin but in case you need to change some configuration here is complete description of the extension

```groovy
getTextConfig {

	/**
	 * Global file encoding configuration, can be overridden by each task
	 * extension.
	 *
	 * Default to: ```'UTF-8'```.
	 */
	encoding = 'UTF-8'

	/**
	 * Global potFile configuration, can be overridden
	 * by [GetTextGetTextTaskExtension.potFile] and
	 * [GetTextMsgMergeTaskExtension.potFile] separately.
	 *
	 * Default to: ```src/main/resources/i18n/keys.pot```.
	 */
	potFile = layout.projectDirectory.file("src/main/resources/i18n/keys.pot")

	/**
	 * Global i18nDirectory configuration, can be overridden
	 * by [GetTextMsgMergeTaskExtension.i18nDirectory] and
	 * [GetTextMsgFmtTaskExtension.poFiles] separately.
	 *
	 * Default to :
	 * ```
	 * i18nDirectory = src/main/resources/i18n
	 * poFiles = src/main/resources/i18n/** /*.po
	 * ```
	 */
	i18nDirectory = layout.projectDirectory.dir("src/main/resources/i18n")

	/**
	 * Global targetBundle configuration, can be overridden
	 * by [GetTextMsgFmtTaskExtension.targetBundle] and
	 * [GetTextPropertyTaskExtension.targetBundle] separately.
	 *
	 * Default to: ```${project.group}.${project.name}.i18n.Messages```.
	 */
	targetBundle = "${project.group}.${project.name}.i18n.Messages"

	/**
	 *  GetTextTask extension used to configure [GetTextTask] task.
	 */
	getTextTask {

		/** Task description. */
		description = "Extracts translatable strings using xgettext from source files and generates a POT file."

		/** GNU gettext command to use default xgettext. */
		cmd = "xgettext"

		/** Additional executable arguments to be added to cmd. */
		executableArgs = [
				"--package-name=${project.group}.${project.name}",
				"--package-version=${project.version}",
				"-LJava",
				"-n",
				"--no-wrap",
				"-F",
				"--msgid-bugs-address=${project.properties.getOrDefault("developers", "developers")}",
				"-k"
		].toSet()

		/** Override of general plugin encoding for this task */
		encoding = 'UTF-8'

		/** Set of -k keywords to be used to extract translatable strings. */
		keywords = [
				"trc:lc,2",
				"trnc:lc,2,3",
				"tr",
				"mrktr",
				"trn:1,2",
		].toSet()

		/** Set of source files to extract translatable strings from. */
		sourceFiles = layout.projectDirectory.dir("src/main/java").asFileTree.matching { include("**/*.java") }

		/** Override of general plugin setting for generated .pot file with extracted translatable strings. */
		potFile = layout.projectDirectory.dir("src/main/resources/i18n").file("keys.pot")

	}

	/**
	 * GetTextTask extension used to configure [MsgMergeTask] task.
	 */
	msgMergeTask {

		/** Task description. */
		description = "Merge translatable strings using msgmerge from keys.pot to lc.po files."

		/** GNU gettext command to use default msgmerge */
		cmd = "msgmerge"

		/** Additional executable arguments to be added to cmd */
		executableArgs = [
				"--no-wrap",
				"-F",
				"-q"
		].toSet()

		/** Override of general plugin encoding for this task. */
		encoding = 'UTF-8'

		/** Override of general plugin setting for generated .pot file with extracted translatable strings. */
		potFile = layout.projectDirectory.dir("src/main/resources/i18n").file("keys.pot")

		/** Override of general plugin i18n Directory containing lc.po files. */
		i18nDirectory = layout.projectDirectory.dir("src/main/resources/i18n")
	}

	/**
	 * GetTextTask extension used to configure [MsgFmtTask] task.
	 */
	msgFmtTask {

		/** Task description. */
		description = "Generate java bundle resources and classes from po files."

		/** GNU gettext command to use default msgfmt */
		cmd = "msgfmt"

		/** Additional executable arguments to be added to cmd */
		executableArgs = [
				"--java2",
				"-c",
				"-f"
		].toSet()

		/** Override of general plugin encoding for this task. */
		encoding = 'UTF-8'

		/** Set of po files to generate java bundle from. */
		poFiles = layout.getProjectDirectory().dir("src/main/resources/i18n").asFileTree.filter { it.name.endsWith(".po") }

		/** Output directory for generated java bundle. */
		outputDirectory = layout.buildDirectory.dir("classes/java/main")

		/** Override of general plugin target bundle setting. */
		targetBundle = "${project.group}.${project.name}.i18n.Messages"

		/** If true, check if all strings are translated and fail if not. */
		checkTranslated = true
	}

	/**
	 * GetTextTask extension used to configure [GetTextPropertyTask].
	 */
	getTextPropertyTask {

		/** Task description. */
		description = "Generate java bundle resources and classes from po files."

		/** Override of general plugin encoding for this task. */
		encoding = 'UTF-8'

		/** Override of general plugin target bundle setting. */
		targetBundle = "${project.group}.${project.name}.i18n.Messages"

		/** Generated properties configuration file. */
		i18nPropertiesFile = layout.buildDirectory.file("resources/main/i18n.properties")

		/** Generated default translation properties file. */
		defaultTranslationPropertyFile = project.layout.buildDirectory.file("resources/main/${project.group.replace(".", "/")}/${project.name}/i18n/Messages.properties")
	}
}
```
