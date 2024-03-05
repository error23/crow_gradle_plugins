package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task that initializes poetry project structure.
 */
abstract class PoetryInitProjectStructureTask : PoetryBaseTask() {

	/**
	 * Python project name.
	 */
	@get:Input
	abstract val projectName: Property<String>

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
		mainSourcesDirectory.get().dir(projectName.get()).asFile.mkdirs()
		mainSourcesDirectory.get().dir(projectName.get()).file("__init__.py").asFile.createNewFile()

		mainResourcesDirectory.get().dir(projectName.get() + "_res").asFile.mkdirs()
		mainResourcesDirectory.get().dir(projectName.get() + "_res").file("__init__.py").asFile.createNewFile()

		testSourcesDirectory.get().dir("test_" + projectName.get()).asFile.mkdirs()
		testSourcesDirectory.get().dir("test_" + projectName.get()).file("__init__.py").asFile.createNewFile()

		testResourcesDirectory.get().dir("test_" + projectName.get() + "_res").asFile.mkdirs()
		testResourcesDirectory.get().dir("test_" + projectName.get() + "_res").file("__init__.py").asFile.createNewFile()

	}
}
