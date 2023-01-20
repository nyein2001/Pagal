package com.service.mediataggingapp.view

import com.service.mediataggingapp.model.ProjectInfo

interface ProjectView {
    fun showLoading()
    fun hideLoading()
    fun setProject(projectList: List<ProjectInfo>)
    fun onErrorLoading(message: String?)
}