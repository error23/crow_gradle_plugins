package com.crow.gradle.plugins.poetry

import com.crow.gradle.plugins.poetry.tasks.PoetryConfigInitTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitEnvironmentTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitProjectStructureTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitPyProjectTask
import com.crow.gradle.plugins.poetry.tasks.PoetryInitReadMeTask
import com.crow.gradle.plugins.poetry.tasks.idea.PoetryIdeaSetupWorkspace
import com.crow.gradle.plugins.poetry.tasks.idea.PoetryIdeaSyncModuleTask
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

		// Create linux packaging extension
		val extension = project.extensions.create<PoetryExtension>("poetryConfig")
		setupExtension(extension)

		// Register tasks
		val poetryConfigInit = project.tasks.register<PoetryConfigInitTask>("poetryConfigInit") {
			description = extension.poetryConfigInitTask.description.get()
			poetryCmd.set(extension.poetryConfigInitTask.poetryCmd)
			poetryTomlFile.set(extension.poetryConfigInitTask.poetryTomlFile)
		}

		val poetryInitProjectStructure = project.tasks.register<PoetryInitProjectStructureTask>("poetryInitProjectStructure") {
			description = extension.poetryInitProjectStructureTask.description.get()
			poetryCmd.set(extension.poetryInitProjectStructureTask.poetryCmd)
			projectName.set(extension.poetryInitProjectStructureTask.projectName)
			mainSourcesDirectory.set(extension.poetryInitProjectStructureTask.mainSourcesDirectory)
			mainResourcesDirectory.set(extension.poetryInitProjectStructureTask.mainResourcesDirectory)
			testSourcesDirectory.set(extension.poetryInitProjectStructureTask.testSourcesDirectory)
			testResourcesDirectory.set(extension.poetryInitProjectStructureTask.testResourcesDirectory)
		}

		val poetryInitReadMe = project.tasks.register<PoetryInitReadMeTask>("poetryInitReadMe") {
			description = extension.poetryInitReadMeTask.description.get()
			poetryCmd.set(extension.poetryInitReadMeTask.poetryCmd)
			projectName.set(extension.poetryInitReadMeTask.projectName)
			projectDescription.set(extension.poetryInitReadMeTask.projectDescription)
			readmeFile.set(extension.poetryInitReadMeTask.readmeFile)
		}

		val poetryInitPyProject = project.tasks.register<PoetryInitPyProjectTask>("poetryInitPyProject") {
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

		val poetryInitEnvironment = project.tasks.register<PoetryInitEnvironmentTask>("poetryInitEnvironment") {
			group = taskGroup
			description = extension.poetryInitEnvironmentTask.description.get()
			poetryCmd.set(extension.poetryInitEnvironmentTask.poetryCmd)
			projectPythonVersion.set(extension.poetryInitEnvironmentTask.projectPythonVersion)
			virtualEnvironmentDirectory.set(extension.poetryInitEnvironmentTask.virtualEnvironmentDirectory)

			dependsOn(poetryConfigInit, poetryInitProjectStructure, poetryInitReadMe, poetryInitPyProject)
		}

		project.tasks.register<PoetryIdeaSyncModuleTask>("PoetryIdeaSyncModule") {
			description = "Sync idea module.iml file with poetry project."
			ideaModuleFile.set(extension.poetryIdeaSyncTask.moduleImlFile)
			jdkName.set(extension.poetryIdeaSyncTask.jdkName)
			mainSourcesDirectory.set(extension.poetryIdeaSyncTask.mainSourcesDirectory)
			mainResourcesDirectory.set(extension.poetryIdeaSyncTask.mainResourcesDirectory)
			testSourcesDirectory.set(extension.poetryIdeaSyncTask.testSourcesDirectory)
			testResourcesDirectory.set(extension.poetryIdeaSyncTask.testResourcesDirectory)

			shouldRunAfter(poetryInitEnvironment)
		}

		project.tasks.register<PoetryIdeaSetupWorkspace>("PoetryIdeaSetupWorkspace") {
			group = taskGroup
			description = "Setup IntelliJ idea workspace for poetry project."
			workspaceFile.set(extension.poetryIdeaSyncTask.workspaceFile)
		}

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
		extension.poetryInitEnvironmentTask.poetryCmd.convention(extension.poetryCmd)

		// Set up main sources directory convention
		extension.poetryInitProjectStructureTask.mainSourcesDirectory.convention(extension.mainSourcesDirectory)
		extension.poetryInitPyProjectTask.mainSourcesDirectory.convention(extension.mainSourcesDirectory)
		extension.poetryIdeaSyncTask.mainSourcesDirectory.convention(with(extension.mainSourcesDirectory) { if (this.get().asFile.exists()) this.get() else null })

		// Set up main resources directory convention
		extension.poetryInitProjectStructureTask.mainResourcesDirectory.convention(extension.mainResourcesDirectory)
		extension.poetryInitPyProjectTask.mainResourcesDirectory.convention(extension.mainResourcesDirectory)
		extension.poetryIdeaSyncTask.mainResourcesDirectory.convention(with(extension.mainResourcesDirectory) { if (this.get().asFile.exists()) this.get() else null })

		// Set up test sources directory convention
		extension.poetryInitProjectStructureTask.testSourcesDirectory.convention(extension.testSourcesDirectory)
		extension.poetryInitPyProjectTask.testSourcesDirectory.convention(extension.testSourcesDirectory)
		extension.poetryIdeaSyncTask.testSourcesDirectory.convention(with(extension.testSourcesDirectory) { if (this.get().asFile.exists()) this.get() else null })

		// Set up test resources directory convention
		extension.poetryInitProjectStructureTask.testResourcesDirectory.convention(extension.testResourcesDirectory)
		extension.poetryInitPyProjectTask.testResourcesDirectory.convention(extension.testResourcesDirectory)
		extension.poetryIdeaSyncTask.testResourcesDirectory.convention(with(extension.testResourcesDirectory) { if (this.get().asFile.exists()) this.get() else null })

		// Set up project name convention
		extension.poetryInitProjectStructureTask.projectName.convention(extension.projectName)
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
		extension.poetryInitEnvironmentTask.projectPythonVersion.convention(extension.projectPythonVersion)

	}
}
