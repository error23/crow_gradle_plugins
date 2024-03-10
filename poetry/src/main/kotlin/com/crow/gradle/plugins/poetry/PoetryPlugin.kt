package com.crow.gradle.plugins.poetry

import com.crow.gradle.plugins.poetry.tasks.PoetryBuildTask
import com.crow.gradle.plugins.poetry.tasks.PoetryTestTask
import com.crow.gradle.plugins.poetry.tasks.PoetryUpdateTask
import com.crow.gradle.plugins.poetry.tasks.PoetryVersionTask
import com.crow.gradle.plugins.poetry.tasks.idea.PoetryIdeaSetupWorkspace
import com.crow.gradle.plugins.poetry.tasks.idea.PoetryIdeaSyncModuleTask
import com.crow.gradle.plugins.poetry.tasks.init.PoetryConfigInitTask
import com.crow.gradle.plugins.poetry.tasks.init.PoetryInitEnvironmentTask
import com.crow.gradle.plugins.poetry.tasks.init.PoetryInitProjectStructureTask
import com.crow.gradle.plugins.poetry.tasks.init.PoetryInitPyProjectTask
import com.crow.gradle.plugins.poetry.tasks.init.PoetryInitReadMeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
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

		// Create configuration for distribution artifacts
		val implementationConfiguration = project.configurations.create("pythonImplementation")
		implementationConfiguration.isCanBeResolved = true
		implementationConfiguration.isCanBeConsumed = false

		val archivesConfiguration = project.configurations.create("pythonArchives")
		archivesConfiguration.isCanBeResolved = false
		archivesConfiguration.isCanBeConsumed = true

		// Create poetry extension
		val extension = project.extensions.create<PoetryExtension>("poetryConfig")
		setupExtension(extension)

		// Register tasks
		val poetryInitEnvironment = registerPoetryInitEnvironmentTasks(project, extension)
		registerPoetryIdeaSyncTasks(project, extension.poetryIdeaSyncTask, poetryInitEnvironment)
		val poetryBuild = registerPoetryGeneralBuildTasks(project, extension)

		// Add artifacts to archives configuration
		project.artifacts.add(archivesConfiguration.name, poetryBuild.get().buildDirectory) {
			this.builtBy("poetryBuild")
		}
	}

	/**
	 * Register poetry general build tasks.
	 *
	 * @param project to register tasks
	 * @param extension to use
	 * @return poetry build task
	 */
	private fun registerPoetryGeneralBuildTasks(project: Project, extension: PoetryExtension): TaskProvider<PoetryBuildTask> {

		val poetryVersion = project.tasks.register<PoetryVersionTask>("poetryVersion") {
			description = extension.poetryVersionTask.description.get()
			poetryCmd.set(extension.poetryVersionTask.poetryCmd)
			projectVersion.set(extension.poetryVersionTask.projectVersion)
			pyprojectFile.set(extension.poetryVersionTask.pyprojectFile)

		}

		val poetryUpdate = project.tasks.register<PoetryUpdateTask>("poetryUpdate") {
			group = taskGroup
			description = extension.poetryUpdateTask.description.get()
			poetryCmd.set(extension.poetryUpdateTask.poetryCmd)
			poetryExtraArgs.set(extension.poetryUpdateTask.poetryExtraArgs)
			release.set(extension.poetryUpdateTask.release)
			pyprojectFile.set(extension.poetryUpdateTask.pyprojectFile)
			poetryLockFile.set(extension.poetryUpdateTask.poetryLockFile)

			dependsOn(poetryVersion)
		}

		val poetryTest = project.tasks.register<PoetryTestTask>("poetryTest") {
			group = "verification"
			description = extension.poetryTestTask.description.get()
			poetryCmd.set(extension.poetryTestTask.poetryCmd)
			testCmd.set(extension.poetryTestTask.testCmd)
			testCmdExtraArgs.set(extension.poetryTestTask.testCmdExtraArgs)
			sourceFiles.from(extension.poetryTestTask.sourceFiles)
			testReportArgument.set(extension.poetryTestTask.testReportArgument)
			testReport.set(extension.poetryTestTask.testReport)

			dependsOn(poetryUpdate)
		}
		project.tasks.named("check").configure {
			dependsOn(poetryTest)
		}

		val poetryBuild = project.tasks.register<PoetryBuildTask>("poetryBuild") {
			group = "build"
			description = extension.poetryBuildTask.description.get()
			poetryCmd.set(extension.poetryBuildTask.poetryCmd)
			poetryExtraArgs.set(extension.poetryBuildTask.poetryExtraArgs)
			sourceFiles.from(extension.poetryBuildTask.sourceFiles)
			buildDirectory.set(extension.poetryBuildTask.buildDirectory)

			dependsOn(poetryUpdate)
		}

		project.tasks.named("assemble").configure {
			dependsOn(poetryBuild)
		}

		return poetryBuild

	}

	/**
	 * Register poetry init environment tasks.
	 *
	 * @param project to register tasks
	 * @param extension to use
	 * @return poetry init environment task
	 */
	private fun registerPoetryInitEnvironmentTasks(project: Project, extension: PoetryExtension): TaskProvider<PoetryInitEnvironmentTask> {

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
			projectVersion.set(extension.poetryInitPyProjectTask.projectVersion)
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

		return poetryInitEnvironment

	}

	/**
	 * Register poetry idea sync tasks.
	 *
	 * @param project to register tasks
	 * @param poetryIdeaSyncTaskExtension to use
	 * @param poetryInitEnvironment to depend on
	 */
	private fun registerPoetryIdeaSyncTasks(project: Project, poetryIdeaSyncTaskExtension: PoetryIdeaSyncTaskExtension, poetryInitEnvironment: TaskProvider<PoetryInitEnvironmentTask>) {

		project.tasks.register<PoetryIdeaSyncModuleTask>("PoetryIdeaSyncModule") {
			description = "Sync idea module.iml file with poetry project."
			ideaModuleFile.set(poetryIdeaSyncTaskExtension.moduleImlFile)
			jdkName.set(poetryIdeaSyncTaskExtension.jdkName)
			mainSourcesDirectory.set(poetryIdeaSyncTaskExtension.mainSourcesDirectory)
			mainResourcesDirectory.set(poetryIdeaSyncTaskExtension.mainResourcesDirectory)
			testSourcesDirectory.set(poetryIdeaSyncTaskExtension.testSourcesDirectory)
			testResourcesDirectory.set(poetryIdeaSyncTaskExtension.testResourcesDirectory)

			shouldRunAfter(poetryInitEnvironment)
		}

		project.tasks.register<PoetryIdeaSetupWorkspace>("PoetryIdeaSetupWorkspace") {
			group = taskGroup
			description = "Setup IntelliJ idea workspace for poetry project."
			workspaceFile.set(poetryIdeaSyncTaskExtension.workspaceFile)
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
		extension.poetryVersionTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryUpdateTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryTestTask.poetryCmd.convention(extension.poetryCmd)
		extension.poetryBuildTask.poetryCmd.convention(extension.poetryCmd)

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

		// Set up project version convention
		extension.poetryInitPyProjectTask.projectVersion.convention(extension.projectVersion)
		extension.poetryVersionTask.projectVersion.convention(extension.projectVersion)

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

		// Set up pyproject file convention
		extension.poetryInitPyProjectTask.pyprojectFile.convention(extension.pyprojectFile)
		extension.poetryVersionTask.pyprojectFile.convention(extension.pyprojectFile)
		extension.poetryUpdateTask.pyprojectFile.convention(extension.pyprojectFile)

		// Set up release convention
		extension.poetryUpdateTask.release.convention(extension.release)

	}
}
