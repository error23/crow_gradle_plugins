package com.crow.gradle.plugins.poetry.tasks.idea

import org.dom4j.Element
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * Task that sets up IntelliJ idea workspace for poetry project.
 */
abstract class PoetryIdeaSetupWorkspace : DefaultTask() {

	/**
	 * Workspace xml file.
	 */
	@get:InputFile
	abstract val workspaceFile: RegularFileProperty

	@TaskAction
	fun execute() {
		val xmlParser = XmlParser(workspaceFile.get().asFile)

		val stateNode = xmlParser.parse("/project/component[@name='ExternalProjectsManager']/system[@id='GRADLE']/state").firstOrNull()
		if (stateNode !is Element) throw GradleException("Failed to find state node in workspace file.")

		var taskStateNode = stateNode.elements("task").firstOrNull { it.attribute("path").value == "\$PROJECT_DIR$" }
		if (taskStateNode == null) taskStateNode = stateNode.addElement("task").addAttribute("path", "\$PROJECT_DIR$")
		if (taskStateNode !is Element) throw GradleException("Failed to find task state node in workspace file.")

		val activationNode = taskStateNode.element("activation") ?: taskStateNode.addElement("activation")
		val beforeSyncNode = activationNode.element("before_sync") ?: activationNode.addElement("before_sync")
		val afterSyncNode = activationNode.element("after_sync") ?: activationNode.addElement("after_sync")

		if (beforeSyncNode.elements("task").none { it.attribute("name").value == "PoetryIdeaSyncModule" }) {
			beforeSyncNode.addElement("task").addAttribute("name", "PoetryIdeaSyncModule")
		}

		if (afterSyncNode.elements("task").none { it.attribute("name").value == "PoetryIdeaSyncModule" }) {
			afterSyncNode.addElement("task").addAttribute("name", "PoetryIdeaSyncModule")
		}

		xmlParser.write()

	}
}
