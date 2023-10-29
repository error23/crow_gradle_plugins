package com.crow.gradle.plugins.packaging.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input

/** Base class for linux-packaging tasks. */
abstract class LinuxPackagingBaseTask : DefaultTask() {

	/**
	 * Different linux distributions to package.
	 *
	 * Example : setOf("Debian", "RedHat")
	 *
	 * @see [LinuxPackagingInitTask.distributionDirectory]
	 */
	@get:Input
	abstract val packageTypes: SetProperty<String>

}
