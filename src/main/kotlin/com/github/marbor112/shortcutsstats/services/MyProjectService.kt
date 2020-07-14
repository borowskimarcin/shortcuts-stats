package com.github.marbor112.shortcutsstats.services

import com.intellij.openapi.project.Project
import com.github.marbor112.shortcutsstats.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
