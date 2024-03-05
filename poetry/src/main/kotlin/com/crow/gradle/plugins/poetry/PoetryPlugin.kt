package com.crow.gradle.plugins.poetry

import com.crow.gradle.plugins.poetry.tasks.PoetryConfigInitTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitProjectStructureTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitPyProjectTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitReadMeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * Poetry plugin.
 * Build and manage Python projects with poetry.
 */
class PoetryPlugin : Plugin<Project> {

	private val taskGroup = "python"

	override fun apply(project: Project) {

		// Apply dependency plugins
		project.plugins.apply("base")
		//project.plugins.apply("idea")

		// Create linux packaging extension
		val extension = project.extensions.create<PoetryExtension>("poetryConfig")
		setupExtension(extension)

		// Register tasks
		project.tasks.register<PoetryConfigInitTask>("poetryConfigInit") {
			group = taskGroup
			description = extension.poetryConfigInitTask.description.get()
			poetryCmd.set(extension.poetryConfigInitTask.poetryCmd)
			poetryTomlFile.set(extension.poetryConfigInitTask.poetryTomlFile)
		}

		val poetryInitProjectStructure = project.tasks.register<PoetryInitProjectStructureTask>("poetryInitProjectStructure") {
			group = taskGroup
			description = extension.poetryInitProjectStructureTask.description.get()
			poetryCmd.set(extension.poetryInitProjectStructureTask.poetryCmd)
			mainSourcesDirectory.set(extension.poetryInitProjectStructureTask.mainSourcesDirectory)
			mainResourcesDirectory.set(extension.poetryInitProjectStructureTask.mainResourcesDirectory)
			testSourcesDirectory.set(extension.poetryInitProjectStructureTask.testSourcesDirectory)
			testResourcesDirectory.set(extension.poetryInitProjectStructureTask.testResourcesDirectory)
		}

		val poetryInitReadMe = project.tasks.register<PoetryInitReadMeTask>("poetryInitReadMe") {
			group = taskGroup
			description = extension.poetryInitReadMeTask.description.get()
			poetryCmd.set(extension.poetryInitReadMeTask.poetryCmd)
			projectName.set(extension.poetryInitReadMeTask.projectName)
			projectDescription.set(extension.poetryInitReadMeTask.projectDescription)
			readmeFile.set(extension.poetryInitReadMeTask.readmeFile)
		}

		project.tasks.register<PoetryInitPyProjectTask>("poetryInitPyProject") {
			group = taskGroup
			description = extension.poetryInitPyProjectTask.description.get()
			poetryCmd.set(extension.poetryInitPyProjectTask.poetryCmd)
			projectName.set(extension.poetryInitPyProjectTask.projectName)
			projectDescription.set(extension.poetryInitPyProjectTask.projectDescription)
			projectAuthor.set(extension.poetryInitPyProjectTask.projectAuthor)
			projectHomepage.set(extension.poetryInitPyProjectTask.projectHomepage)
			projectRepository.set(extension.poetryInitPyProjectTask.projectRepository)
			readmeFile.set(extension.poetryInitPyProjectTask.readmeFile)
			projectPythonVersion.set(extension.poetryInitPyProjectTask.projectPythonVersion)
			mainSourcesDirectory.set(extension.poetryInitPyProjectTask.mainSourcesDirectory)
			mainResourcesDirectory.set(extension.poetryInitPyProjectTask.mainResourcesDirectory)
			testSourcesDirectory.set(extension.poetryInitPyProjectTask.testSourcesDirectory)
			testResourcesDirectory.set(extension.poetryInitPyProjectTask.testResourcesDirectory)
			pyprojectFile.set(extension.poetryInitPyProjectTask.pyprojectFile)

			dependsOn(poetryInitProjectStructure, poetryInitReadMe)
		}

		/*
		project.tasks.withType(GenerateIdeaModule::class.java).configureEach {

			group = taskGroup
			val moduleImlDirectoryPath = project.gradle.rootProject.rootProject.projectDir.toString() + "/.idea/modules" + project.projectDir.toString().replace(project.gradle.rootProject.rootProject.projectDir.toString(), "")
			val moduleImlFileName = project.gradle.rootProject.name + project.projectDir.toString().replace(project.gradle.rootProject.rootProject.projectDir.toString(), "").replace("/", ".") + ".iml"

			inputFile = File(moduleImlDirectoryPath, moduleImlFileName)
			outputFile = File(moduleImlDirectoryPath, moduleImlFileName)
			module.jdkName = "Poetry (${project.name})"

			module.sourceDirs.add(File(project.projectDir, "src/main/python"))
			module.resourceDirs.add(File(project.projectDir, "src/main/resources"))
			module.testSources.from(File(project.projectDir, "src/test/python"))
			module.testResources.from(File(project.projectDir, "src/test/resources"))

		}
		*/
	}

	/**
	 * Setup global extension conventions.
	 *
	 * @param extension to set up
	 */
	private fun setupExtension(extension: PoetryExtension) {

		// Set up poetryCmd convention
		extension.poetryConfigInitTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryInitProjectStructureTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryInitReadMeTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryInitPyProjectTask.poetryCmd.convention(extension.poetryCmd)

		// Set up main sources directory convention
		extension.poetryInitProjectStructureTask.mainSourcesDirectory.convention(extension.mainSourcesDirectory)
		extension.poetryInitPyProjectTask.mainSourcesDirectory.convention(extension.mainSourcesDirectory)

		// Set up main resources directory convention
		extension.poetryInitProjectStructureTask.mainResourcesDirectory.convention(extension.mainResourcesDirectory)
		extension.poetryInitPyProjectTask.mainResourcesDirectory.convention(extension.mainResourcesDirectory)

		// Set up test sources directory convention
		extension.poetryInitProjectStructureTask.testSourcesDirectory.convention(extension.testSourcesDirectory)
		extension.poetryInitPyProjectTask.testSourcesDirectory.convention(extension.testSourcesDirectory)

		// Set up test resources directory convention
		extension.poetryInitProjectStructureTask.testResourcesDirectory.convention(extension.testResourcesDirectory)
		extension.poetryInitPyProjectTask.testResourcesDirectory.convention(extension.testResourcesDirectory)

		// Set up project name convention
		extension.poetryInitReadMeTask.projectName.convention(extension.projectName)
		extension.poetryInitPyProjectTask.projectName.convention(extension.projectName)

		// Set up project description convention
		extension.poetryInitReadMeTask.projectDescription.convention(extension.projectDescription)
		extension.poetryInitPyProjectTask.projectDescription.convention(extension.projectDescription)

		// Set up readme file convention
		extension.poetryInitReadMeTask.readmeFile.convention(extension.readmeFile)
		extension.poetryInitPyProjectTask.readmeFile.convention(extension.readmeFile)

		// Set up project author convention
		extension.poetryInitPyProjectTask.projectAuthor.convention(extension.projectAuthor)

		// Set up project python version convention
		extension.poetryInitPyProjectTask.projectPythonVersion.convention(extension.projectPythonVersion)

	}
}
