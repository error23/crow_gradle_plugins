package com.crow.gradle.plugins.poetry.tasks.init

import com.crow.gradle.plugins.poetry.tasks.PoetryBaseTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task to initialize a new poetry project environment from gradle build script.
 */
abstract class PoetryInitEnvironmentTask : PoetryBaseTask() {

	/**
	 * Python project version.
	 */
	@get:Input
	abstract val projectPythonVersion: Property<String>

	/**
	 * Directory containing virtual environment.
	 */
	@get:OutputDirectory
	abstract val virtualEnvironmentDirectory: DirectoryProperty

	init {
		this.onlyIf { !virtualEnvironmentDirectory.get().asFile.exists() }
	}

	@TaskAction
	fun execute() {

		if (!virtualEnvironmentDirectory.get().asFile.delete()) {
			throw GradleException("Failed to delete virtual environment directory.")
		}

		runPoetry(setOf(
		  "env",
		  "use",
		  projectPythonVersion.get())
		)

		runPoetry(setOf(
		  "install",
		  "--sync")
		)
	}

}
