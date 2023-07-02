package com.crow.gradle.plugins.gettext

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/** Generate properties from targetBundle for gettext-commons library. */
abstract class GetTextPropertyTask : BaseGetTextTask() {

    /** Target bundle name to generate properties. */
    @get:Input
    abstract val targetBundle: Property<String>

    /** Generated properties configuration file. */
    @get:OutputFile
    abstract val i18nPropertiesFile: RegularFileProperty

    /** Generated default translation properties file. */
    @get:OutputFile
    abstract val defaultTranslationPropertyFile: RegularFileProperty

    /**
     * Creates i18n properties file with basename and default translation
     * properties file for gettext-commons library.
     */
    @TaskAction
    fun execute() {
        i18nPropertiesFile.get().asFile.writeText("basename = " + targetBundle.get(), charset(encoding.get()))
        defaultTranslationPropertyFile.get().asFile.createNewFile()

    }
}
