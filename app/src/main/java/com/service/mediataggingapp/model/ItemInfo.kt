package com.service.mediataggingapp.model

data class ItemInfo(
    var name: String? = null,
    var photos: MutableList<String>? = ArrayList(),
    var tags: MutableList<Hashtag>? = ArrayList(),
    var created_at: String? = null,
    var last_modified_at: String? = null
)