package com.crow.gradle.plugins.gettext

import java.io.ByteArrayOutputStream
import java.io.File
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

/** Generate java bundle resources and classes from po files. */
abstract class MsgFmtTask : BaseGetTextTask() {

    /** Set of po files to generate java bundle from. */
    @get:InputFiles
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val poFiles: ConfigurableFileCollection

    /** Output directory for generated java bundle. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    /** Target bundle name. */
    @get:Input
    abstract val targetBundle: Property<String>

    /** If true, check if all strings are translated and fail if not. */
    @get:Input
    abstract val checkTranslated: Property<Boolean>

    /** Target bundle output path. */
    @Internal
    val bundleOutput = targetBundle.map { it.replace('.', File.separatorChar) }

    /**
     * Executes msgFmt command and generates java bundle from po files.
     *
     * @param inputChanges incremental changes descriptor
     */
    @TaskAction
    fun execute(inputChanges: InputChanges) {

        for (po in inputChanges.getFileChanges(poFiles)) {

            if (po.fileType != FileType.FILE) continue
            if (po.changeType == ChangeType.REMOVED) {
                project.delete(
                  outputDirectory.get().file(
                    bundleOutput.get() + "_" + po.file.toLocaleString() + ".class"
                  )
                )
                continue
            }

            val stderr = ByteArrayOutputStream()

            project.exec {
                executable = cmd.get()
                args(executableArgs.get())
                if (logger.isInfoEnabled) args("--verbose")
                args("-d", outputDirectory.get().asFile.relativeTo(project.projectDir).path)
                args("-r", targetBundle.get())
                args("-l", po.file.toLocaleString())
                args("--statistics")
                args(po.file.relativeTo(project.projectDir).path)
                errorOutput = stderr

            }
            checkError(stderr.toString(), po.file.name)
        }
    }

    private fun checkError(message: String, poFileName: String) {

        val errorMessage = message.replace(".*uses unchecked or unsafe operations.*\n".toRegex(), "")
          .replace(".*unchecked for details.*\n".toRegex(), "").trimEnd()

        // if last message contains no (it means that there are no translated texts)
        if (errorMessage.lines().last().contains(".*no?. .*".toRegex())) {
            // if checkTranslated is true throw exception
            if (checkTranslated.get()) {
                throw RuntimeException("$errorMessage file : $poFileName")
            }
            // else if is the only message log it as error
            else if (errorMessage.lines().size == 1) {
                logger.error(errorMessage)
            }
        }

        // if there is more than one message than log them as errors
        if (errorMessage.lines().size > 1) {
            logger.error(errorMessage)
        }
        // If there is only one message log it as info since we already logged no translation message as error
        // and --statistics writes into stderr no meter what
        else {
            logger.info(errorMessage)
        }
    }
}
