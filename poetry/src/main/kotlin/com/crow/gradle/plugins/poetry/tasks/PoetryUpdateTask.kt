package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that updates poetry dependencies.
 */
abstract class PoetryUpdateTask : PoetryBaseTask() {

	/**
	 *  Poetry command extra arguments.
	 */
	@get:[Input Optional]
	abstract val poetryExtraArgs: SetProperty<String>

	/**
	 * Release flag.
	 */
	@get:[Input Optional]
	abstract val release: Property<Boolean>

	/**
	 * Input pyproject.toml file.
	 */
	@get:InputFile
	abstract val pyprojectFile: RegularFileProperty

	/**
	 * Output poetry.lock file.
	 */
	@get:OutputFile
	abstract val poetryLockFile: RegularFileProperty

	@TaskAction
	fun execute() {
		if (release.isPresent && release.get()) setUpLocalPackagesToRelease()

		val args = mutableSetOf("update")
		if (poetryExtraArgs.isPresent) args += poetryExtraArgs.get()
		runPoetry(args)
	}

	/**
	 * Set up local packages to release.
	 */
	private fun setUpLocalPackagesToRelease() {
		project.configurations.getByName("pythonImplementation").allDependencies.forEach {

			try {
				runPoetry(setOf("remove", it.name))
			} catch (e: Exception) {
				logger.error("Nothing to remove for ${it.name}:${it.version}")
			}
			runPoetry(setOf("add", it.name + "=" + it.version.toString()))
		}
	}
}
