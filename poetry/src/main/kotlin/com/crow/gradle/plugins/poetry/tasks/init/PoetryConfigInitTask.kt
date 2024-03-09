package com.crow.gradle.plugins.poetry.tasks.init

import com.crow.gradle.plugins.poetry.tasks.PoetryBaseTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that initializes poetry configuration file.
 */
abstract class PoetryConfigInitTask : PoetryBaseTask() {

	/**
	 * Poetry configuration file.
	 */
	@get:OutputFile
	abstract val poetryTomlFile: RegularFileProperty

	init {
		this.onlyIf { !poetryTomlFile.get().asFile.exists() }
	}

	@TaskAction
	fun execute() {

		poetryTomlFile.get().asFile.writeText("""
			[virtualenvs]
			in-project = true
		""".trimIndent())

	}
}
