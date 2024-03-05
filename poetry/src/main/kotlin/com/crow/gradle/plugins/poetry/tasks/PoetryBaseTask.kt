package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Bask task for poetry plugin.
 */
abstract class PoetryBaseTask : DefaultTask() {

	/**
	 * Poetry system command.
	 */
	@get:Input
	abstract val poetryCmd: Property<String>

	/**
	 * Run poetry command.
	 * @param poetryCmdArgs set of command arguments.
	 */
	fun runPoetry(poetryCmdArgs: Set<String>) {
		project.exec {
			executable = poetryCmd.get()
			args(poetryCmdArgs.toList())
			if (logger.isInfoEnabled) args("-v")
			if (logger.isDebugEnabled) args("-vv")
			if (logger.isTraceEnabled) args("-vvv")
		}
	}

}
