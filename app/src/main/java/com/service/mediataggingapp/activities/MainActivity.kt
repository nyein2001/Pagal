package com.service.mediataggingapp.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.service.mediataggingapp.R
import com.service.mediataggingapp.adapters.ProjectAdapter
import com.service.mediataggingapp.model.ProjectInfo
import com.service.mediataggingapp.model.UserDetailsInfo
import com.service.mediataggingapp.utils.InitTheme
import com.service.mediataggingapp.utils.NetworkCheck.isConnected
import com.service.mediataggingapp.utils.Presenter
import com.service.mediataggingapp.utils.Tools
import com.service.mediataggingapp.view.ProfileView
import com.service.mediataggingapp.view.ProjectView
import java.util.*


class MainActivity : AppCompatActivity(), ProjectView, ProfileView {

    @BindView(R.id.material_toolbar)
    lateinit var toolbar: MaterialToolbar

    @BindView(R.id.profile)
    lateinit var profileBtn: CardView

    @BindView(R.id.profile_image)
    lateinit var profileImage: ImageView

    @BindView(R.id.project_recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var presenter: Presenter
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        presenter = Presenter(this, this)
        initComponent()
        toolbarItemAction()
        showData()
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
    }

    private fun toolbarItemAction() {
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_project -> {
                    addProjectDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showData() {
        presenter.getProjects()
        presenter.getUserInfo()
    }

    private fun addProjectDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = LayoutInflater.from(this).inflate(
            R.layout.new_product_dialog,
            findViewById<ConstraintLayout>(
                R.id.new_product_dialog_container
            )
        )

        val edtProjectName: EditText = view.findViewById(R.id.edt_project_name)
        val cancelBtn: Button = view.findViewById(R.id.cancel)
        val createBtn: Button = view.findViewById(R.id.create_project)

        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        createBtn.setOnClickListener {
            if (!TextUtils.isEmpty(edtProjectName.text.toString())) {
                val id = db.collection("projects").document().id
                val projectInfo = ProjectInfo(
                    id,
                    edtProjectName.text.toString(),
                    Calendar.getInstance().time.toString(),
                    Calendar.getInstance().time.toString()
                )
                db.collection("projects")
                    .add(projectInfo)
                    .addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Successfully Added ",
                            Toast.LENGTH_SHORT
                        ).show()
                        alertDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            applicationContext,
                            "New project adding is not success",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w("TAG", "Error add document", e)
                        alertDialog.dismiss()
                    }
            } else {
                //show warning
            }
        }
        alertDialog.show()
    }

    private fun showNoInternetDialog() {
    }

    override fun onStart() {
        super.onStart()
        if (isConnected(this)) {
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

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun setProfile(profile: UserDetailsInfo) {
       Tools.displayImageOriginal(applicationContext, profileImage, profile.profile_pic!!)
    }

    override fun setProject(projectList: List<ProjectInfo>) {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        projectAdapter = ProjectAdapter(this, projectList)
        recyclerView.adapter = projectAdapter
        projectAdapter.run {
            notifyDataSetChanged()
            setOnItemClickListener(object : ProjectAdapter.ProjectClickListener {
                override fun onClick(view: View, position: Int) {
                    val intent = Intent(applicationContext, ItemActivity::class.java)
                    intent.putExtra("project_name", projectList[position].name)
                    intent.putExtra("created_at", projectList[position].created_at)
                    intent.putExtra("last_modified_at", projectList[position].last_modified_at)
                    startActivity(intent)
                }
            })
        }
    }


    override fun onErrorLoading(message: String?) {

    }
}

