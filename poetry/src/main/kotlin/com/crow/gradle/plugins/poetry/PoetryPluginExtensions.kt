package com.crow.gradle.plugins.poetry

import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.property

/**
 * Poetry plugin base extension. Used to configure poetry plugin tasks.
 */
open class PoetryBaseExtension @Inject constructor(objects: ObjectFactory) {

	/** Task description. */
	val description = objects.property<String>()

	/** Overrides global Poetry command to execute. */
	val poetryCmd = objects.property<String>()

}

/**
 * Poetry plugin config init task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryConfigInitTask] task.
 */
open class PoetryConfigInitTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/** Poetry configuration file. */
	val poetryTomlFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	init {
		description.convention("Initializes poetry configuration file.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin init project structure task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryInitProjectStructureTask] task.
 */
open class PoetryInitProjectStructureTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global Directory containing main python sources.
	 * This is the directory where your main application python code lives.
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Overrides global Directory containing main resources.
	 * This is the directory where your main application resources live.
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Overrides global Directory containing test python sources.
	 * This is the directory where your test python code lives.
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Overrides global Directory containing test resources.
	 * This is the directory where your test resources live.
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/resources"))

	init {
		description.convention("Initializes poetry project structure.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin readme task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryInitReadMeTask] task.
 */
open class PoetryInitReadMeTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global project name.
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Overrides global project description.
	 */
	val projectDescription = objects.property<String>().convention(project.description?.removeSurrounding("'"))

	/**
	 * Overrides global readme file.
	 */
	val readmeFile = objects.fileProperty().convention(project.layout.projectDirectory.file("README.md"))

	init {
		description.convention("Generates README.md file.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin init pyproject task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryInitPyProjectTask] task.
 */
open class PoetryInitPyProjectTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global project name.
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Overrides global project description.
	 */
	val projectDescription = objects.property<String>().convention(project.description?.removeSurrounding("'"))

	/**
	 * Overrides global project author.
	 */
	val projectAuthor = objects.property<String>().convention(project.findProperty("developerName").toString().plus(" <" + project.findProperty("developerEmail").toString() + ">"))

	/**
	 * Project homepage.
	 */
	val projectHomepage = objects.property<String>().convention(project.findProperty("projectUrl")?.toString())

	/**
	 * Project repository.
	 */
	val projectRepository = objects.property<String>().convention(project.findProperty("projectUrl")?.toString())

	/**
	 * Overrides global readme file.
	 */
	@get:InputFile
	val readmeFile = objects.fileProperty().convention(project.layout.projectDirectory.file("README.md"))

	/**
	 * Overrides global python project version.
	 */
	val projectPythonVersion = objects.property<String>()

	/**
	 * Overrides global Directory containing main python sources.
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Overrides global Directory containing main resources.
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Overrides global Directory containing test python sources.
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Overrides global Directory containing test resources.
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/resources"))

	/**
	 * Output pyproject.toml file.
	 */
	@get:OutputFile
	val pyprojectFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	init {
		description.convention("Initializes pyproject.toml file.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin extension.
 * Used to configure poetry plugin tasks.
 */
@Suppress("Unused")
abstract class PoetryExtension @Inject constructor(objects: ObjectFactory, project: Project) {

	/**
	 * Global Poetry command to execute.
	 * @see [PoetryBaseExtension.poetryCmd]
	 */
	val poetryCmd = objects.property<String>().convention("poetry")

	/**
	 * Global Directory containing main python sources.
	 * @see [PoetryInitProjectStructureTaskExtension.mainSourcesDirectory] and [PoetryInitPyProjectTaskExtension.mainSourcesDirectory]
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Global Directory containing main resources.
	 * @see [PoetryInitProjectStructureTaskExtension.mainResourcesDirectory] and [PoetryInitPyProjectTaskExtension.mainResourcesDirectory]
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Global Directory containing test python sources.
	 * @see [PoetryInitProjectStructureTaskExtension.testSourcesDirectory] and [PoetryInitPyProjectTaskExtension.testSourcesDirectory]
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Global Directory containing test resources.
	 * @see [PoetryInitProjectStructureTaskExtension.testResourcesDirectory] and [PoetryInitPyProjectTaskExtension.testResourcesDirectory]
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/resources"))

	/**
	 * Global project name.
	 * @see [PoetryInitReadMeTaskExtension.projectName] and [PoetryInitPyProjectTaskExtension.projectName]
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Global project description.
	 * @see [PoetryInitReadMeTaskExtension.projectDescription] and [PoetryInitPyProjectTaskExtension.projectDescription]
	 */
	val projectDescription = objects.property<String>().convention(project.description?.removeSurrounding("'"))

	/**
	 *  Global readme file.
	 *  @see [PoetryInitReadMeTaskExtension.readmeFile] and [PoetryInitPyProjectTaskExtension.readmeFile]
	 */
	val readmeFile = objects.fileProperty().convention(project.layout.projectDirectory.file("README.md"))

	/**
	 * Global project author.
	 * @see [PoetryInitPyProjectTaskExtension.projectAuthor]
	 */
	val projectAuthor = objects.property<String>().convention(project.findProperty("developerName").toString().plus(" <" + project.findProperty("developerEmail").toString() + ">"))

	/**
	 * Global python project version.
	 * @see [PoetryInitPyProjectTaskExtension.projectPythonVersion]
	 */
	val projectPythonVersion = objects.property<String>()

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryConfigInitTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryConfigInitTask: PoetryConfigInitTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryInitProjectStructureTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitProjectStructureTask: PoetryInitProjectStructureTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryInitReadMeTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitReadMeTask: PoetryInitReadMeTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryInitPyProjectTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitPyProjectTask: PoetryInitPyProjectTaskExtension

	fun poetryConfigInitTask(action: Action<in PoetryConfigInitTaskExtension>) {
		action.execute(poetryConfigInitTask)
	}

	fun poetryInitProjectStructureTask(action: Action<in PoetryInitProjectStructureTaskExtension>) {
		action.execute(poetryInitProjectStructureTask)
	}

	fun poetryInitReadMeTask(action: Action<in PoetryInitReadMeTaskExtension>) {
		action.execute(poetryInitReadMeTask)
	}

	fun poetryInitPyProjectTask(action: Action<in PoetryInitPyProjectTaskExtension>) {
		action.execute(poetryInitPyProjectTask)
	}
}
