# crow_gradle_plugins

Some gradle plugins implementation

# GetTextPlugin

Get text plugin defines usual workflow to generate pot,po and javaBundle files from java code source using GetTextBase plugin

## Usage

If you are using gettext-commons library with java normally you would just need to apply plugin

```groovy
plugins {
	id 'com.crow.gradle.plugins.gettext.GetTextPlugin' version '1.0.0'
}
```

### Advanced usage :

If you need to customize some plugin settings you can use following configuration

```groovy
/*
 * Extension configuration
 */
getText {

	/**
	 * Global file encoding configuration, can be overridden by each task
	 * extension.
	 *
	 * Default to: ```'UTF-8'```.
	 */
	encoding.set('UTF-8')

	/**
	 * Global potFile configuration, can be overridden
	 * by [GetTextGetTextTaskExtension.potFile] and
	 * [GetTextMsgMergeTaskExtension.potFile] separately.
	 *
	 * Default to: ```src/main/resources/i18n/keys.pot```.
	 */
	potFile.set(project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot"))

	/**
	 * Global targetBundle configuration, can be overridden
	 * by [GetTextMsgFmtTaskExtension.targetBundle] and
	 * [GetTextPropertyTaskExtension.targetBundle] separately.
	 *
	 * Default to: ```${project.group}.${project.name}.i18n.Messages```.
	 */
	i18nDirectory.set(project.layout.projectDirectory.dir("src/main/resources/i18n"))

	/**
	 * Global targetBundle configuration, can be overridden
	 * by [GetTextMsgFmtTaskExtension.targetBundle] and
	 * [GetTextPropertyTaskExtension.targetBundle] separately.
	 *
	 * Default to: ```${project.group}.${project.name}.i18n.Messages```.
	 */
	targetBundle.set(project.group.toString() + "." + project.name + ".i18n.Messages")

	/** [GetTextTask] specific configuration. */
	getTextTask {

		/** Path to xgettext executable */
		cmd.set('xgettext')

		/** Extra arguments to pass to xgettext executable */
		executableArgs.set([
				"--package-name=${project.group}.${project.name}",
				"--package-version=${project.version}",
				"-LJava",
				"-n",
				"--no-wrap",
				"-F",
				"--msgid-bugs-address=${project.properties.getOrDefault("developers", "developers")}",
				"-k"])

		/** If set than global encoding for the plugin is ignored in getTextTask */
		encoding.set('UTF-8')

		/**
		 * Set of -k keywords to be used to extract translatable strings. 
		 *
		 * Default to: ["trc:lc,2", "trnc:lc,2,3", "tr", "mrktr", "trn:1,2"]
		 */
		keywords.set(["trc:lc,2", "trnc:lc,2,3", "tr", "mrktr", "trn:1,2"])

		/**
		 * Set of source files to extract translatable strings from. 
		 *
		 * Default to: src/main/java/** / *.java
		 */
		sourceFiles.set(project.layout.projectDirectory.dir("src/main/java").asFileTree.matching() { include("**/*.java") })

		/**
		 * Generated .pot file with extracted translatable strings. 
		 *
		 * Default to: src/main/resources/i18n/keys.pot
		 */
		potFile.set(project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot"))

	}

	/** GetTextTask extension used to configure msgMerge task. */
	msgMergeTask {

		/** Path to msgmerge executable */
		cmd.set('msgmerge')

		/** Extra arguments to pass to xgettext executable */
		executableArgs.set([
				"--no-wrap",
				"-F",
				"-q"])

		/** If set than global encoding for the plugin is ignored in msgMergeTask */
		encoding.set('UTF-8')

		/** keys.pot file with extracted translatable strings */
		potFile = project.layout.projectDirectory.file("src/main/resources/i18n/keys.pot")

		/** Directory containing lc.po files. */
		i18nDirectory = project.layout.projectDirectory.dir("src/main/resources/i18n")

	}

	/** GetTextTask extension used to configure msgFmt task. */
	msgFmtTask {

		/** Path to msgmerge executable */
		cmd.set('msgfmt')

		/** Extra arguments to pass to xgettext executable */
		executableArgs.set([
				"--java2",
				"-c",
				"-C",
				"-f"])

		/** If set than global encoding for the plugin is ignored in msgFmtTask */
		encoding.set('UTF-8')

		/** Set of po files to generate java bundle from. */
		poFiles.set(project.layout.projectDirectory.dir("src/main/resources/i18n").asFileTree.matching() { include("**/*.po") })

		/** Output directory for generated java bundle. */
		outputDirectory(project.layout.buildDirectory.dir("classes/java/main"))

		/** Target bundle name. */
		targetBundle.set(project.group.toString() + "." + project.name + ".i18n.Messages")
	}

	/** GetTextTask extension used to configure property task. */
	generateI18nProperties {

		/** Target bundle name to generate properties. */
		targetBundle.set(project.group.toString() + "." + project.name + ".i18n.Messages")

		/** Generated properties configuration file. */
		i18nPropertiesFile.set(project.layout.projectDirectory.file("src/main/resources/i18n/i18n.properties"))

		/** Generated default translation properties file. */
		defaultTranslationPropertyFile.set(project.layout.projectDirectory.file("src/main/resources/i18n/default.properties"))
	}
}
```

# GetTextBase plugin

GetTextBase plugin defines usual tasks needed to generate pot,po and javaBundle files from java code source

## Usage

```groovy

plugins {
	id 'com.crow.gradle.plugins.gettext.GetTextBasePlugin' version '1.0.0'
}

```

All tasks can be configured using GetTextExtension which is already created and binded by the plugin with name 'gettext'

## GetTextTask

### Inputs :

- keywords : Set of -k keywords to be used to extract translatable strings.
- sourceFiles :  Set of source files to extract translatable strings from.

### Outputs :

- potFile : Generated .pot file with extracted translatable strings.

## MsgMergeTask

### Inputs :

- potFile : keys.pot file with extracted translatable strings.
- i18nDirectory : Directory containing lc.po files.

### Outputs :

- poFiles : Generated and updated .po files with merged translatable strings.

## MsgFmtTask

### Inputs :

- poFiles : Set of po files to generate java bundle from.
- targetBundle : java target bundle

### Outputs :

- outputDirectory : Output directory for generated java bundle.

## GetTextPropertyTask

### Inputs :

- targetBundle : java target bundle

### Outputs :

- i18nPropertiesFile : Generated properties configuration file. containing basename=targetBundle to be used with gettext-commons library.
- defaultTranslationPropertyFile : Generated default translation properties file. containing just an empty translation file needed for the fallback when using i18n.tr() without translation
