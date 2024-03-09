package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that set poetry project version.
 */
abstract class PoetryVersionTask : PoetryBaseTask() {

	/**
	 * Project version.
	 */
	@get:Input
	abstract val projectVersion: Property<String>

	/**
	 * Output pyproject.toml file.
	 */
	@get:OutputFile
	abstract val pyprojectFile: RegularFileProperty

	@TaskAction
	fun execute() {
		runPoetry(setOf("version", projectVersion.get()))
	}
}

