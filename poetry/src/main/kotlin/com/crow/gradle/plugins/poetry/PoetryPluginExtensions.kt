package com.crow.gradle.plugins.poetry

import java.io.File
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

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
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.init.PoetryConfigInitTask] task.
 */
open class PoetryConfigInitTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/** Poetry configuration file. */
	val poetryTomlFile = objects.fileProperty().convention(project.layout.projectDirectory.file("poetry.toml"))

	init {
		description.convention("Initializes poetry configuration file.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin init project structure task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitProjectStructureTask] task.
 */
open class PoetryInitProjectStructureTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global project name.
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Overrides global directory containing main python sources.
	 * This is the directory where your main application python code lives.
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Overrides global directory containing main resources.
	 * This is the directory where your main application resources live.
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Overrides global directory containing test python sources.
	 * This is the directory where your test python code lives.
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Overrides global directory containing test resources.
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
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitReadMeTask] task.
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
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitPyProjectTask] task.
 */
open class PoetryInitPyProjectTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global project name.
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Overrides global project version.
	 */
	val projectVersion = objects.property<String>().convention(project.version.toString().replace("-SNAPSHOT", "a0"))

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
	val readmeFile = objects.fileProperty().convention(project.layout.projectDirectory.file("README.md"))

	/**
	 * Overrides global python project version.
	 */
	val projectPythonVersion = objects.property<String>()

	/**
	 * Overrides global directory containing main python sources.
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Overrides global directory containing main resources.
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Overrides global directory containing test python sources.
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Overrides global directory containing test resources.
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/resources"))

	/**
	 * Overrides global pyproject.toml file.
	 */
	val pyprojectFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	init {
		description.convention("Initializes pyproject.toml file.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin init environment task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitEnvironmentTask] task.
 */
open class PoetryInitEnvironmentTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global python project version.
	 */
	val projectPythonVersion = objects.property<String>()

	/**
	 * Directory containing virtual environment.
	 */
	val virtualEnvironmentDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir(".venv"))

	init {
		description.convention("Initializes poetry environment.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin intellij idea sync task extension.
 * Used to configure IntelliJ idea module and workspace in order to sync with poetry project.
 */
open class PoetryIdeaSyncTaskExtension @Inject constructor(objects: ObjectFactory, project: Project) {

	/**
	 * Module iml file.
	 */
	val moduleImlFile = objects.fileProperty().convention(findModuleImlFile(project))

	/**
	 * Workspace xml file.
	 */
	val workspaceFile = objects.fileProperty().convention(project.rootProject.layout.projectDirectory.dir(".idea").file("workspace.xml"))

	/**
	 * Poetry JDK name in IntelliJ idea.
	 */
	val jdkName = objects.property<String>().convention("Poetry (${project.name})")

	/**
	 * Overrides global directory containing main python sources.
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(with(project.layout.projectDirectory.dir("src/main/python")) { if (this.asFile.exists()) this else null })

	/**
	 * Overrides global directory containing main resources.
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(with(project.layout.projectDirectory.dir("src/main/resources")) { if (this.asFile.exists()) this else null })

	/**
	 * Overrides global directory containing test python sources.
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(with(project.layout.projectDirectory.dir("src/test/python")) { if (this.asFile.exists()) this else null })

	/**
	 * Overrides global directory containing test resources.
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(with(project.layout.projectDirectory.dir("src/test/resources")) { if (this.asFile.exists()) this else null })

	/**
	 * Function that finds module.xml file in intellij .idea folder.
	 */
	private fun findModuleImlFile(project: Project): RegularFile {

		var moduleImlFile = project.layout.projectDirectory.dir(".idea").dir("modules").file(project.name + ".iml")

		if (project.projectDir != project.rootProject.projectDir) {
			moduleImlFile = project.rootProject.layout.projectDirectory.dir(".idea").dir("modules").dir(project.projectDir.relativeTo(project.rootProject.projectDir).toString())
			  .file(project.rootProject.name + "." + project.projectDir.relativeTo(project.rootProject.projectDir).toString().replace(File.separatorChar, '.') + ".iml")
		}

		return moduleImlFile
	}
}

/**
 * Poetry plugin version task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryVersionTask] task.
 */
open class PoetryVersionTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Overrides global project version.
	 */
	val projectVersion = objects.property<String>().convention(project.version.toString().replace("-SNAPSHOT", "a0"))

	/**
	 * Overrides global pyproject.toml file.
	 */
	val pyprojectFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	init {
		description.convention("Sets poetry project version.")
		poetryCmd.convention("poetry")

	}
}

/**
 * Poetry plugin update task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryUpdateTask] task.
 */
open class PoetryUpdateTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Poetry update extra arguments.
	 */
	val poetryExtraArgs = objects.setProperty<String>()

	/**
	 * Overrides global release flag.
	 */
	val release = objects.property<Boolean>().convention(false)

	/**
	 * Overrides global pyproject.toml file.
	 */
	val pyprojectFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	/**
	 * Poetry.lock file.
	 */
	val poetryLockFile = objects.fileProperty().convention(project.layout.projectDirectory.file("poetry.lock"))

	init {
		description.convention("Updates poetry dependencies.")
		poetryCmd.convention("poetry")
	}
}

/**
 * Poetry plugin test task extension.
 * Used to configure [com.crow.gradle.plugins.poetry.tasks.PoetryTestTask] task.
 */
open class PoetryTestTaskExtension @Inject constructor(objects: ObjectFactory, project: Project)
	: PoetryBaseExtension(objects) {

	/**
	 * Python test command.
	 */
	val testCmd = objects.property<String>().convention("pytest")

	/**
	 * Test command extra arguments.
	 */
	val testCmdExtraArgs = objects.setProperty<String>().convention(setOf("--color=yes"))

	/**
	 * Source files to track changements.
	 */
	val sourceFiles = objects.fileCollection().from(project.layout.projectDirectory.dir("src/").asFileTree.matching { include("**/*.py") })

	/**
	 * Test report argument option to generate test report.
	 */
	val testReportArgument = objects.property<String>().convention("--html=")

	/**
	 * Test report output file.
	 */
	val testReport = objects.fileProperty().convention(project.layout.buildDirectory.file("reports/tests/test.html"))

	init {
		description.convention("Runs poetry tests.")
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
	 * Global poetry command to execute.
	 * @see [PoetryBaseExtension.poetryCmd]
	 */
	val poetryCmd = objects.property<String>().convention("poetry")

	/**
	 * Global directory containing main python sources.
	 * @see [PoetryInitProjectStructureTaskExtension.mainSourcesDirectory] and [PoetryInitPyProjectTaskExtension.mainSourcesDirectory]
	 */
	val mainSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/python"))

	/**
	 * Global directory containing main resources.
	 * @see [PoetryInitProjectStructureTaskExtension.mainResourcesDirectory] and [PoetryInitPyProjectTaskExtension.mainResourcesDirectory]
	 */
	val mainResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/main/resources"))

	/**
	 * Global directory containing test python sources.
	 * @see [PoetryInitProjectStructureTaskExtension.testSourcesDirectory] and [PoetryInitPyProjectTaskExtension.testSourcesDirectory]
	 */
	val testSourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/python"))

	/**
	 * Global directory containing test resources.
	 * @see [PoetryInitProjectStructureTaskExtension.testResourcesDirectory] and [PoetryInitPyProjectTaskExtension.testResourcesDirectory]
	 */
	val testResourcesDirectory = objects.directoryProperty().convention(project.layout.projectDirectory.dir("src/test/resources"))

	/**
	 * Global project name.
	 * @see [PoetryInitReadMeTaskExtension.projectName] and [PoetryInitPyProjectTaskExtension.projectName]
	 */
	val projectName = objects.property<String>().convention(project.name)

	/**
	 * Global project version.
	 * @see [PoetryInitPyProjectTaskExtension.projectVersion]
	 */
	val projectVersion = objects.property<String>().convention(project.version.toString().replace("-SNAPSHOT", "a0"))

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
	 * @see [PoetryInitPyProjectTaskExtension.projectPythonVersion] and [PoetryInitEnvironmentTaskExtension.projectPythonVersion]
	 */
	val projectPythonVersion = objects.property<String>()

	/**
	 * Global release flag.
	 * If true gradle will replace all local project dependencies with their online versions.
	 */
	val release = objects.property<Boolean>().convention(false)

	/**
	 * Global pyproject.toml file.
	 * @see [PoetryInitPyProjectTaskExtension.pyprojectFile] and [PoetryVersionTaskExtension.pyprojectFile] and [PoetryUpdateTaskExtension.pyprojectFile]
	 */
	val pyprojectFile = objects.fileProperty().convention(project.layout.projectDirectory.file("pyproject.toml"))

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.init.PoetryConfigInitTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryConfigInitTask: PoetryConfigInitTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitProjectStructureTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitProjectStructureTask: PoetryInitProjectStructureTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitReadMeTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitReadMeTask: PoetryInitReadMeTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitPyProjectTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitPyProjectTask: PoetryInitPyProjectTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.init.PoetryInitEnvironmentTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryInitEnvironmentTask: PoetryInitEnvironmentTaskExtension

	/**
	 * Intellij idea sync task specific configuration.
	 */
	@get:Nested
	abstract val poetryIdeaSyncTask: PoetryIdeaSyncTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryVersionTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryVersionTask: PoetryVersionTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryUpdateTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryUpdateTask: PoetryUpdateTaskExtension

	/**
	 * [com.crow.gradle.plugins.poetry.tasks.PoetryTestTask]
	 * specific configuration.
	 */
	@get:Nested
	abstract val poetryTestTask: PoetryTestTaskExtension

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

	fun poetryInitEnvironmentTask(action: Action<in PoetryInitEnvironmentTaskExtension>) {
		action.execute(poetryInitEnvironmentTask)
	}

	fun poetryIdeaSyncTask(action: Action<in PoetryIdeaSyncTaskExtension>) {
		action.execute(poetryIdeaSyncTask)
	}

	fun poetryVersionTask(action: Action<in PoetryVersionTaskExtension>) {
		action.execute(poetryVersionTask)
	}

	fun poetryUpdateTask(action: Action<in PoetryUpdateTaskExtension>) {
		action.execute(poetryUpdateTask)
	}

	fun poetryTestTask(action: Action<in PoetryTestTaskExtension>) {
		action.execute(poetryTestTask)
	}
}
