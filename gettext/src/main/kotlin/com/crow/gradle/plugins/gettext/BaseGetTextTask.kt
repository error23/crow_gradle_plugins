package com.crow.gradle.plugins.gettext

import java.io.File
import java.nio.charset.Charset
import java.util.Locale
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/** Base class for getText tasks. */
abstract class BaseGetTextTask : DefaultTask() {

	/** Command to execute. */
	@get:Input
	@get:Optional
	abstract val cmd: Property<String>

	/** Command extra arguments. */
	@get:Input
	abstract val executableArgs: SetProperty<String>

	/** File encoding. */
	@get:Input
	abstract val encoding: Property<String>

	/**
	 * Sets header encoding (charset) value of the po file.
	 *
	 * @param encoding encoding to set
	 */
	protected fun File.setEncoding(encoding: String) {

		val content = readBytes()
		val encodingString = "Content-Type: text/plain; charset=CHARSET".toByteArray()
		val encodingStringHolder = "CHARSET".toByteArray()

		var headerStart = content.indices.find { i ->
			encodingString.withIndex().all { (j, v) -> v == content[i + j] }
		} ?: return

		headerStart -= encodingStringHolder.size

		outputStream().use {
			it.write(content, 0, headerStart + encodingString.size)
			it.write(encoding.toByteArray())
			it.write(content, headerStart + encodingString.size + encodingStringHolder.size, content.size - headerStart - encodingString.size - encodingStringHolder.size)
		}
	}

	/**
	 * Updates header of the po file with the one from the pot file.
	 *
	 * @param potFile pot file to get header from
	 */
	protected fun File.updateHeader(potFile: File) {

		val date = potFile.readText(Charset.forName(encoding.get()))
		  .lines()
		  .find {
			  it.startsWith("\"POT-Creation-Date:")
		  } ?: return

		val updated = readText(Charset.forName(encoding.get()))
		  .replaceFirst(".*POT-Creation-Date:.*".toRegex(), date.replace("\\", "\\\\"))
		  .replaceFirst("\"Plural-Forms: nplurals=INTEGER; plural=EXPRESSION;\\n\"", "\"Plural-Forms: nplurals=2; plural=(n > 1);\\n\"")

		writeText(updated, Charset.forName(encoding.get()))
	}

	/**
	 * Converts file name to [Locale.toString].
	 *
	 * @return locale string
	 */
	protected fun File.toLocaleString(): String {

		val tokens = nameWithoutExtension.split('_').toMutableList()
		if (tokens.size < 3) {
			val variants = tokens.last().split('@', limit = 2)
			if (variants.size > 1) {
				tokens.removeLast()
				tokens.add(variants[0])
				tokens.add(variants[1])
			}
		}

		val builder = Locale.Builder()

		val scrypt = nameWithoutExtension.split('#', limit = 2)
		if (scrypt.size > 1) {
			builder.setScript(scrypt[1])
			tokens.removeLast()
		}

		when (tokens.size) {
			1 -> builder.setLanguage(tokens[0])
			2 -> builder.setLanguage(tokens[0]).setRegion(tokens[1])
			3 -> builder.setLanguage(tokens[0]).setRegion(tokens[1]).setVariant(tokens[2])
			else -> throw GradleException("Invalid locale $nameWithoutExtension")
		}

		val locale = builder.build()
		require(Locale.getAvailableLocales().contains(locale)) {
			throw GradleException("Locale $locale is not available on this system")
		}

		return locale.toString()
	}
}
