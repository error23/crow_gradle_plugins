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
			
			## Building
			This project uses Gradle and poetry as a build tool.
			You should use Gradle to build project and poetry to manage python dependencies except local dependencies since Poetry is incapable to manage local dependencies path correctly.
			
			* To install poetry follow the instructions on the official website: https://python-poetry.org/docs/
			* This project is using gradle wrapper so you don't need to install gradle. Just use the provided gradlew script.
			
			To build the project run the following command in project root directory:
			```shell
			./gradlew build
			```
			
		""".trimIndent())

	}
}
