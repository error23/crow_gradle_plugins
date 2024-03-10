package com.crow.gradle.plugins.poetry.tasks

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task that updates poetry dependencies.
 */
abstract class PoetryUpdateTask : PoetryBaseTask() {

	/**
	 * Release flag.
	 */
	@get:[Input Optional]
	abstract val release: Property<Boolean>

	@TaskAction
	fun execute() {
		if (release.isPresent && release.get()) setUpLocalPackagesToRelease()
		runPoetry(setOf("update"))
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
