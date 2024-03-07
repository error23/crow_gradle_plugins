package com.crow.gradle.plugins.poetry.tasks.idea

import org.dom4j.Element
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Task that syncs idea module.iml files with poetry project.
 */
abstract class PoetryIdeaSyncModuleTask : DefaultTask() {

	/**
	 * Module iml file.
	 */
	@get:InputFile
	abstract val ideaModuleFile: RegularFileProperty

	/**
	 * Poetry JDK name in IntelliJ idea.
	 */
	@get:Input
	abstract val jdkName: Property<String>

	/**
	 * Directory containing main python sources.
	 */
	@get:[InputDirectory Optional]
	abstract val mainSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing main resources.
	 */
	@get:[InputDirectory Optional]
	abstract val mainResourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test python sources.
	 */
	@get:[InputDirectory Optional]
	abstract val testSourcesDirectory: DirectoryProperty

	/**
	 * Directory containing test resources.
	 */
	@get:[InputDirectory Optional]
	abstract val testResourcesDirectory: DirectoryProperty

	init {
		this.onlyIf { ideaModuleFile.get().asFile.exists() }
	}

	@TaskAction
	fun execute() {

		val xmlParser = XmlParser(ideaModuleFile.get().asFile)
		setUpJdk(xmlParser)
		setUpSourceDirectories(xmlParser)
		xmlParser.write()

	}

	/**
	 * Set up JDK in module.xml file.
	 * @param xmlParser to use.
	 */
	private fun setUpJdk(xmlParser: XmlParser) {
		var inheritedJdkNode = xmlParser.parse("/module/component/orderEntry[@type='inheritedJdk']").firstOrNull()
		if (inheritedJdkNode == null) inheritedJdkNode = xmlParser.parse("/module/component/orderEntry[@type='jdk']").firstOrNull()

		if (inheritedJdkNode is Element) {
			inheritedJdkNode.addAttribute("type", "jdk")
			inheritedJdkNode.addAttribute("jdkName", jdkName.get())
			inheritedJdkNode.addAttribute("jdkType", "Python SDK")
		}
	}

	/**
	 * Set up source directories in module.xml file.
	 * @param xmlParser to use.
	 */
	private fun setUpSourceDirectories(xmlParser: XmlParser) {

		// Parse xml
		val contentNode = xmlParser.parse("/module/component/content").firstOrNull()
		if (contentNode !is Element) throw GradleException("Content node not found in module.iml file.")
		val sourcesFolderNodes = xmlParser.parse("/module/component/content/sourceFolder")

		// map urls to source directories
		val sourceDirectories = mapModuleDirectoryUrlToSourceDirectory(contentNode)

		// Update existing source directories
		for (sourceFolderNode in sourcesFolderNodes) {

			if (sourceFolderNode !is Element) continue
			val url = sourceFolderNode.attributeValue("url") ?: continue
			val directory = sourceDirectories[url] ?: continue
			addAttributesToElement(sourceFolderNode, url, directory)
			sourceDirectories.remove(url)
		}

		for ((url, directory) in sourceDirectories) {
			addAttributesToElement(contentNode.addElement("sourceFolder"), url, directory)
		}
	}

	/**
	 * Add attributes to source folder element.
	 * @param sourceFolderNode to add attributes to.
	 * @param url attribute to add to sourceFolderNode.
	 * @param directory to add attributes from to sourceFolderNode.
	 */
	private fun addAttributesToElement(sourceFolderNode: Element, url: String, directory: DirectoryProperty) {
		sourceFolderNode.addAttribute("url", url)
		when (directory) {
			mainSourcesDirectory -> sourceFolderNode.addAttribute("isTestSource", "false")
			mainResourcesDirectory -> sourceFolderNode.addAttribute("type", "java-resource")
			testSourcesDirectory -> sourceFolderNode.addAttribute("isTestSource", "true")
			testResourcesDirectory -> sourceFolderNode.addAttribute("type", "java-test-resource")
		}
	}

	/**
	 * Map module directory url to source directories.
	 * @param contentNode to use.
	 * @return map of urls to source directories.
	 */
	private fun mapModuleDirectoryUrlToSourceDirectory(contentNode: Element): HashMap<String, DirectoryProperty> {

		// Get module directory url
		val moduleDirectoryUrl = contentNode.attributeValue("url") ?: throw GradleException("Module directory url not found in module.iml file.")

		// Map urls to source directories
		val sourceDirectories = HashMap<String, DirectoryProperty>()

		val directories = listOf(mainSourcesDirectory, mainResourcesDirectory, testSourcesDirectory, testResourcesDirectory)
		for (directory in directories) {
			if (directory.isPresent) {
				sourceDirectories[moduleDirectoryUrl + "/" + directory.get().asFile.relativeTo(project.projectDir).path] = directory
			}
		}


		return sourceDirectories

	}

}
