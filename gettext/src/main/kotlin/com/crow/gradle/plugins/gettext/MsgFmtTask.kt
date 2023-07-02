package com.crow.gradle.plugins.gettext

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

    /** Target bundle output output path. */
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

            project.exec {
                executable = cmd.get()
                args(executableArgs.get())
                if (logger.isInfoEnabled) args("--verbose")
                args("-d", outputDirectory.get().asFile.relativeTo(project.projectDir).path)
                args("-r", targetBundle.get())
                args("-l", po.file.toLocaleString())
                args(po.file.relativeTo(project.projectDir).path)
            }
        }
    }
}
