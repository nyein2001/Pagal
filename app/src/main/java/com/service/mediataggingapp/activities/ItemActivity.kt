package com.service.mediataggingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.service.mediataggingapp.R
import com.service.mediataggingapp.adapters.PostAdapter
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.utils.InitTheme
import com.service.mediataggingapp.utils.Presenter
import com.service.mediataggingapp.view.ItemView

class ItemActivity : AppCompatActivity(), ItemView {

    @BindView(R.id.material_toolbar)
    lateinit var toolbar: MaterialToolbar

    @BindView(R.id.posts_recycler_view)
    lateinit var postsRecyclerView: RecyclerView

    private lateinit var presenter: Presenter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_item)
        ButterKnife.bind(this)
        presenter = Presenter(this)
        initComponent()
        toolbarItemAction()
        showData()
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        val storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
    }

    private fun toolbarItemAction() {
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.tags -> {
                    val intent = Intent(this, TagsActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.add_item -> {
                    val intent = Intent(this, AddNewItemActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun showData() {
        presenter.getPosts()
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun setProject(posts: List<ItemInfo>) {
        postsRecyclerView.layoutManager = GridLayoutManager(this, 4)
        postsRecyclerView.setHasFixedSize(false)
        postsRecyclerView.itemAnimator = DefaultItemAnimator()
        postAdapter = PostAdapter(this, posts)
        postsRecyclerView.adapter = postAdapter
    }

    override fun onErrorLoading(message: String?) {
    }
}