package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task that initializes poetry project structure.
 */
abstract class PoetryInitProjectStructureTask : PoetryBaseTask() {

	/**
	 * Directory containing main python sources.
	 */
	@get:OutputDirectory
	abstract val mainSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing main resources.
	 */
	@get:OutputDirectory
	abstract val mainResourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test python sources.
	 */
	@get:OutputDirectory
	abstract val testSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test resources.
	 */
	@get:OutputDirectory
	abstract val testResourcesDirectory: DirectoryProperty

	@TaskAction
	fun execute() {

		// Create project structure
		mainSourcesDirectory.get().asFile.mkdirs()
		mainResourcesDirectory.get().asFile.mkdirs()
		testSourcesDirectory.get().asFile.mkdirs()
		testResourcesDirectory.get().asFile.mkdirs()

	}
}
