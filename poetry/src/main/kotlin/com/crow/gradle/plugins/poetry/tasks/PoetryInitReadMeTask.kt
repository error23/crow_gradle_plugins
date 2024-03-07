package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that initializes poetry project README file.
 */
abstract class PoetryInitReadMeTask : PoetryBaseTask() {

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
	 * Readme file.
	 */
	@get:OutputFile
	abstract val readmeFile: RegularFileProperty

	init {
		this.onlyIf { !readmeFile.get().asFile.exists() }
	}

	@TaskAction
	fun execute() {

		readmeFile.get().asFile.writeText("""
			# ${projectName.get()}
			${projectDescription.get()}
			
			## Getting Started
			These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
			
			### Prerequisites
			
			* This project is using gradle wrapper so you don't need to install gradle. Just use the provided gradlew script.
			* Python 
			* Poetry
				* To install poetry follow the instructions on the official website: https://python-poetry.org/docs/
			
			## Building with Gradle and Poetry
			This project uses Gradle and poetry as a build tool.
			You should use Gradle to build project and poetry to manage python dependencies except local dependencies since Poetry is incapable to manage local dependencies path correctly.
			
			To build the project run the following command in project root directory:
			```shell
			./gradlew build
			```
			
			## Setting up IntelliJ IDEA
			
			### Importing the project
			
			1. Open IntelliJ IDEA
			2. Click on "Open or Import" button
			3. Select the project root directory
			4. Click "Open"
			5. Select "Import project from external model" and choose "Gradle"
			6. Click "Next" and "Finish"
			7. Wait for the project to be imported
			8. Open Settings (Ctrl+Alt+S)
			9. Navigate to "Build, Execution, Deployment" -> "Build Tools" -> "Gradle"
			10. Select generate *.iml files for modules imported from gradle
			11. Open "Gradle" tool window and run "PoetryIdeaSetupWorkspace" task
			
			### Configure IntelliJ IDEA Poetry SDK
			
			1. Open project structure (Ctrl+Alt+Shift+S)
			2. Navigate to "Platform Settings" -> "SDKs"
			3. Click on "+" button and select "Add Python SDK"
			4. Navigate to "Poetry Environment" and chose to add "Existing environment"
			5. Select interpreter (...) button and navigate to poetry python executable located in your_module_directory/.venv/bin/python
			6. Click "OK" and "Apply"
			7. Sdk name should be named "Poetry (your_module_name)"
			8. repeat steps 3 - 8 for each module in your project
			9. Open "Gradle" tool window and sync gradle project which should run "PoetryIdeaSyncModule" task for each Python module if you have run "PoetryIdeaSetupWorkspace" task before
			10. Open project structure (Ctrl+Alt+Shift+S) and navigate to "Modules" normally you should see all your python modules with "Poetry (your_module_name)" sdk
			
			
		""".trimIndent())

	}
}
