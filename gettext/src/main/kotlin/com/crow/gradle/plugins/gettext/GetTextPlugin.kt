package com.crow.gradle.plugins.gettext

import org.gradle.api.Project

/** GetText plugin with implemented java workflow. */
class GetTextPlugin : GetTextBasePlugin() {

    override fun apply(project: Project) {
        super.apply(project)

        project.tasks.named("msgMerge").configure {
            dependsOn("getText")
        }

        project.tasks.named("classes").configure {
            dependsOn("msgFmt")
        }

        project.tasks.named("processResources").configure {
            dependsOn("generateI18nProperties")
        }
    }
}
