package com.service.mediataggingapp.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.model.ProjectInfo
import com.service.mediataggingapp.view.ItemView
import com.service.mediataggingapp.view.ProjectView
import com.service.mediataggingapp.view.TagView

class Presenter {

    private lateinit var projectView: ProjectView
    private lateinit var itemView: ItemView
    private lateinit var tagView: TagView

    private lateinit var db: FirebaseFirestore

    constructor(projectView: ProjectView) {
        this.projectView = projectView
    }

    constructor(itemView: ItemView) {
        this.itemView = itemView
    }

    constructor(tagView: TagView) {
        this.tagView = tagView
    }

    fun getProjects() {
        db = Firebase.firestore
        db.collection("projects")
            .get()
            .addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        if (task.result != null) {
                            val projectList: List<ProjectInfo> =
                                task.result.toObjects(ProjectInfo::class.java)
                            projectView.setProject(projectList)
                        } else {
                            Log.d("TAG", "Task Result is Empty")
                        }
                    } else {
                        Log.d("TAG", "Task is unsuccessful")
                    }
                }
            }
            .addOnFailureListener { exception ->
                run {
                    Log.d("TAG", "addOnFailureListener" + exception.message)
                    exception.printStackTrace()
                }

            }
    }

    fun getTags() {
        db = Firebase.firestore
        db.collection("hashtags")
            .get()
            .addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        if (task.result != null) {
                            val hashtags: List<Hashtag> = task.result.toObjects(Hashtag::class.java)
                            tagView.setProject(hashtags)
                        } else {
                            Log.d("TAG", "Task Result is Empty")
                        }
                    } else {
                        Log.d("TAG", "Task is unsuccessful")
                    }
                }
            }
            .addOnFailureListener { exception ->
                run {
                    Log.d("TAG", "addOnFailureListener" + exception.message)
                    exception.printStackTrace()
                }

            }
    }

    fun getPosts() {
        db = Firebase.firestore
        db.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        if (task.result != null) {
                            val posts: List<ItemInfo> = task.result.toObjects(ItemInfo::class.java)
                            itemView.setProject(posts)
                        } else {
                            Log.d("TAG", "Task Result is Empty")
                        }
                    } else {
                        Log.d("TAG", "Task is unsuccessful")
                    }
                }
            }
            .addOnFailureListener { exception ->
                run {
                    Log.d("TAG", "addOnFailureListener" + exception.message)
                    exception.printStackTrace()
                }

            }
    }
}