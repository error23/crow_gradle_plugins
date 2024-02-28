package com.crow.gradle.plugins.poetry

import org.gradle.api.Plugin
import org.gradle.api.Project

class GetTextPlugin : Plugin<Project> {

	private val taskGroup = "i18n"

	override fun apply(project: Project) {
		println("GetTextPlugin apply")

	}

}
