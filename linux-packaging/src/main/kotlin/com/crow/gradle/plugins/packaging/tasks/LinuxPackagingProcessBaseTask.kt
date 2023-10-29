package com.crow.gradle.plugins.packaging.tasks

import java.io.File
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileTree
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.filter

/** Base class for linux-packaging (sources/ressources) Processing tasks. */
abstract class LinuxPackagingProcessBaseTask : LinuxPackagingBaseTask() {

	/**
	 * Include pattern for the copy task.
	 *
	 * @see [CopySpec.include]
	 */
	@get:Input
	@get:Optional
	abstract val copyInclude: SetProperty<String>

	/**
	 * Exclude pattern for the copy task.
	 *
	 * @see [CopySpec.exclude]
	 */
	@get:Input
	@get:Optional
	abstract val copyExclude: SetProperty<String>

	/**
	 * Properties to replace in the files.
	 *
	 * @see [ReplaceTokens]
	 */
	@get:Input
	@get:Optional
	abstract val filterProperties: MapProperty<String, Any>

	/**
	 * Add include pattern to the copy task.
	 *
	 * @see [CopySpec.include]
	 */
	protected fun CopySpec.addInclude() {
		if (copyInclude.isPresent) {
			include(copyInclude.get())
		}
	}

	/**
	 * Add exclude pattern to the copy task.
	 *
	 * @see [CopySpec.exclude]
	 */
	protected fun CopySpec.addExclude() {
		if (copyExclude.isPresent) {
			exclude(copyExclude.get())
		}
	}

	/**
	 * Add replace tokens filter to the copy task.
	 *
	 * @see [ReplaceTokens]
	 */
	protected fun CopySpec.addReplaceTokensFilter() {
		if (filterProperties.isPresent) {
			filter<ReplaceTokens>("tokens" to filterProperties.get(), "beginToken" to "@", "endToken" to "@")
		}
	}

	/**
	 * Unarchive the file.
	 *
	 * Supported archive types are : zip, jar, tar, tar.gz, tar.bz2, tar.xz
	 *
	 * @return unarchived file tree
	 */
	protected fun File.unArchive(): FileTree {

		when (this.extension) {
			"zip" -> return project.zipTree(this)
			"jar" -> return project.zipTree(this)
			"tar" -> return project.tarTree(this)
			"tar.gz" -> return project.tarTree(this)
			"tar.bz2" -> return project.tarTree(this)
			"tar.xz" -> return project.tarTree(this)
		}

		throw IllegalArgumentException("Unsupported archive type: ${this.extension}")

	}
}
