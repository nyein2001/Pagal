package com.service.mediataggingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.service.mediataggingapp.R
import com.service.mediataggingapp.adapters.PostAdapter
import com.service.mediataggingapp.adapters.ProjectAdapter
import com.service.mediataggingapp.adapters.TagAdapter
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.model.ProjectInfo
import com.service.mediataggingapp.model.UserDetailsInfo
import com.service.mediataggingapp.utils.InitTheme
import com.service.mediataggingapp.utils.NetworkCheck
import com.service.mediataggingapp.utils.Presenter
import com.service.mediataggingapp.utils.Tools
import com.service.mediataggingapp.view.ItemView
import com.service.mediataggingapp.view.ProfileView
import com.service.mediataggingapp.view.ProjectView
import com.service.mediataggingapp.view.TagView

class ProfileActivity : AppCompatActivity(), ProfileView, ProjectView, ItemView, TagView {

    @BindView(R.id.user_profile)
    lateinit var profilePic: ImageView

    @BindView(R.id.user_name)
    lateinit var userName: TextView

    @BindView(R.id.no_projects)
    lateinit var noProjects: TextView

    @BindView(R.id.no_posts)
    lateinit var noPosts: TextView

    @BindView(R.id.no_tags)
    lateinit var noTags: TextView

    @BindView(R.id.profile_back_btn)
    lateinit var backBtn: ImageView

    @BindView(R.id.profile_night_mode_switch)
    lateinit var nightModeSwitch: MaterialCardView

    @BindView(R.id.profile_night_mode_switch_title)
    lateinit var nightModeSwitchTitle: TextView

    @BindView(R.id.profile_night_mode_switch_icon)
    lateinit var nightModeSwitchIcon: ImageView

    @BindView(R.id.user_project_recycler_view)
    lateinit var userProjectRecyclerView: RecyclerView

    @BindView(R.id.user_tag_recycler_view)
    lateinit var userTagRecyclerView: RecyclerView

    @BindView(R.id.user_post_recycler_view)
    lateinit var userPostRecyclerView: RecyclerView

    private lateinit var presenter: Presenter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var init: InitTheme
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var tagAdapter: TagAdapter
    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ButterKnife.bind(this)
        initTheme()
        presenter = Presenter(this, this, this, this)
        initComponent()
        showData()
        changeTheme()
    }

    private fun initTheme() {
        init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            nightModeSwitchIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.mode_day,
                    null
                )
            )
            nightModeSwitchTitle.text = getString(R.string.switch_light)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            nightModeSwitchIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.mode_night,
                    null
                )
            )
            nightModeSwitchTitle.text = getString(R.string.switch_night)
        }
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }

    private fun showData() {
        presenter.getUserInfo()
        presenter.getProjects()
        presenter.getPosts()
        presenter.getTags()
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun setTags(hashtags: List<Hashtag>) {
        userTagRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userTagRecyclerView.setHasFixedSize(false)
        userTagRecyclerView.itemAnimator = DefaultItemAnimator()
        tagAdapter = TagAdapter(this, hashtags)
        userTagRecyclerView.adapter = tagAdapter
    }

    override fun setProject(projectList: List<ProjectInfo>) {
        userProjectRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        userProjectRecyclerView.setHasFixedSize(false)
        userProjectRecyclerView.itemAnimator = DefaultItemAnimator()
        projectAdapter = ProjectAdapter(this, projectList)
        userProjectRecyclerView.adapter = projectAdapter
    }

    override fun setItems(items: List<ItemInfo>) {
        userPostRecyclerView.layoutManager = GridLayoutManager(this, 4)
        userPostRecyclerView.setHasFixedSize(false)
        userPostRecyclerView.itemAnimator = DefaultItemAnimator()
        postAdapter = PostAdapter(this, items)
        userPostRecyclerView.adapter = postAdapter
    }

    override fun setProfile(profileInfo: UserDetailsInfo) {
        userName.text = profileInfo.name
        Tools.displayImageOriginal(this, profilePic, profileInfo.profile_pic!!)
    }

    override fun onErrorLoading(message: String?) {
    }

    private fun changeTheme() {
        nightModeSwitch.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                init.setIsNightModeEnabled(true)
                val intent = Intent(this, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
            } else {
                init.setIsNightModeEnabled(false)
                val intent = Intent(this, ProfileActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                finish()
                startActivity(intent)
            }
        }
    }

    private fun showNoInternetDialog() {
    }

    override fun onStart() {
        super.onStart()
        if (NetworkCheck.isConnected(this)) {
            val user = auth.currentUser
            if (user == null) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            showNoInternetDialog()
        }
    }
}