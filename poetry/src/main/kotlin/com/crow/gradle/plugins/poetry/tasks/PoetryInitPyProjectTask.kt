package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that initializes pyproject.toml file.
 */
abstract class PoetryInitPyProjectTask : PoetryBaseTask() {

	/**
	 * Python project name.
	 */
	@get:Input
	abstract val projectName: Property<String>

	/**
	 * Python project description.
	 */
	@get:Input
	abstract val projectDescription: Property<String>

	/**
	 * Python project author.
	 */
	@get:Input
	abstract val projectAuthor: Property<String>

	/**
	 * Project homepage.
	 */
	@get:Input
	@get:Optional
	abstract val projectHomepage: Property<String>

	/**
	 * Project repository.
	 */
	@get:Input
	@get:Optional
	abstract val projectRepository: Property<String>

	/**
	 * Output README file path.
	 */
	@get:InputFile
	abstract val readmeFile: RegularFileProperty

	/**
	 * Python project version.
	 */
	@get:Input
	abstract val projectPythonVersion: Property<String>

	/**
	 * Directory containing main python sources.
	 */
	@get:InputDirectory
	abstract val mainSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing main resources.
	 */
	@get:InputDirectory
	abstract val mainResourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test python sources.
	 */
	@get:InputDirectory
	abstract val testSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test resources.
	 */
	@get:InputDirectory
	abstract val testResourcesDirectory: DirectoryProperty

	/**
	 * Output pyproject.toml file.
	 */
	@get:OutputFile
	abstract val pyprojectFile: RegularFileProperty

	init {
		this.onlyIf { !pyprojectFile.get().asFile.exists() }
	}

	@TaskAction
	fun execute() {

		val firstPart = """
			[tool.poetry]
			name = "${projectName.get()}"
			version = "0.0.1"
			description = "${projectDescription.get()}"
			authors = ["${projectAuthor.get()}"]
			maintainers = ["${projectAuthor.get()}"]
			readme = "${readmeFile.get().asFile.relativeTo(project.projectDir)}"
			""".trimIndent()

		val thirdPart = """ 
			
			packages = [
			    { include = "${projectName.get()}", from = "${mainSourcesDirectory.get().asFile.relativeTo(project.projectDir)}" },
			    { include = "${projectName.get()}_res", from = "${mainResourcesDirectory.get().asFile.relativeTo(project.projectDir)}" }
			]

			[tool.poetry.dependencies]
			python = "^${projectPythonVersion.get()}"
			
			[tool.poetry.group.test]
			optional = true
			
			[tool.poetry.group.test.dependencies]
			pytest = "*"
			
			[tool.pytest.ini_options]
			testpaths = ["${testSourcesDirectory.get().asFile.relativeTo(project.projectDir)}", "${testResourcesDirectory.get().asFile.relativeTo(project.projectDir)}"]

			[build-system]
			requires = ["poetry-core"]
			build-backend = "poetry.core.masonry.api"
			
		""".trimIndent()

		var fileContent = firstPart

		if (projectHomepage.isPresent) {
			fileContent = fileContent.plus("\nhomepage = \"${projectHomepage.get()}\"\n")
		}
		if (projectRepository.isPresent) {
			fileContent = fileContent.plus("repository = \"${projectRepository.get()}\"\n")
		}

		fileContent = fileContent.plus(thirdPart)

		pyprojectFile.get().asFile.writeText(fileContent)
	}
}
