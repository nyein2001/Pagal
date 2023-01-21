package com.service.mediataggingapp.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.model.ProjectInfo
import com.service.mediataggingapp.model.UserDetailsInfo
import com.service.mediataggingapp.view.ItemView
import com.service.mediataggingapp.view.ProfileView
import com.service.mediataggingapp.view.ProjectView
import com.service.mediataggingapp.view.TagView

class Presenter {

    private lateinit var profileView: ProfileView
    private lateinit var projectView: ProjectView
    private lateinit var itemView: ItemView
    private lateinit var tagView: TagView

    private lateinit var db: FirebaseFirestore

    constructor(itemView: ItemView) {
        this.itemView = itemView
    }

    constructor(tagView: TagView) {
        this.tagView = tagView
    }

    constructor(projectView: ProjectView, profileView: ProfileView) {
        this.projectView = projectView
        this.profileView = profileView
    }

    constructor(
        profileView: ProfileView,
        projectView: ProjectView,
        itemView: ItemView,
        tagView: TagView
    ) {
        this.profileView = profileView
        this.projectView = projectView
        this.itemView = itemView
        this.tagView = tagView
    }

    fun getUserInfo() {
        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid

        Log.d("*** CurrentUser ***", uid)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("users")
            .child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData: UserDetailsInfo = snapshot.getValue<UserDetailsInfo>()!!
                    profileView.setProfile(userData)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
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
                            tagView.setTags(hashtags)
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
                            itemView.setItems(posts)
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