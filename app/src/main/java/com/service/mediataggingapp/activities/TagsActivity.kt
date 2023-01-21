package com.service.mediataggingapp.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.service.mediataggingapp.adapters.TagAdapter
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.utils.InitTheme
import com.service.mediataggingapp.utils.NetworkCheck.isConnected
import com.service.mediataggingapp.utils.Presenter
import com.service.mediataggingapp.utils.Tools
import com.service.mediataggingapp.view.TagView
import java.util.Calendar
import java.util.UUID

class TagsActivity : AppCompatActivity(), TagView {

    companion object {
        private const val PICK_IMAGE_REQUEST: Int = 1001
    }

    @BindView(R.id.material_toolbar)
    lateinit var toolbar: MaterialToolbar

    @BindView(R.id.add_new_tag)
    lateinit var addNewTag: TextView

    @BindView(R.id.tag_recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var presenter: Presenter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var filePath: Uri
    private lateinit var hashtag: Hashtag
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_tags)
        ButterKnife.bind(this)
        presenter = Presenter(this)
        initComponent()
        toolbarItemAction()
        showData()
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        val storage: FirebaseStorage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        hashtag = Hashtag()
    }

    private fun toolbarItemAction() {
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, ItemActivity::class.java)
            startActivity(intent)
        }

        addNewTag.setOnClickListener { addNewTagDialog() }
    }

    private fun showData() {
        presenter.getTags()
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun setTags(hashtags: List<Hashtag>) {
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.setHasFixedSize(false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        tagAdapter = TagAdapter(this, hashtags)
        recyclerView.adapter = tagAdapter
    }

    override fun onErrorLoading(message: String?) {
        TODO("Not yet implemented")
    }

    private fun addNewTagDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.new_tag_dialog,
            findViewById<ConstraintLayout>(R.id.new_tag_dialog_container)
        )


        val tagImage: ImageView = view.findViewById(R.id.tag_icon)
        val edtTagName: EditText = view.findViewById(R.id.edt_tag_name)
        val cancelBtn: Button = view.findViewById(R.id.cancel)
        val createButton: Button = view.findViewById(R.id.create_project)

        builder.setView(view)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        tagImage.setOnClickListener {
            chooseImage()
            if (hashtag.file != null) {
                Tools.displayImageOriginal(this, tagImage, hashtag.file!!)
            }
        }

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        createButton.setOnClickListener {
            if (!TextUtils.isEmpty(edtTagName.text.toString())) {
                hashtag.name = edtTagName.text.toString()
                hashtag.created_at = Calendar.getInstance().time.toString()
                hashtag.last_modified_at = Calendar.getInstance().time.toString()
                db.collection("hashtags")
                    .add(hashtag)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Successfully Added", Toast.LENGTH_SHORT)
                            .show()
                        alertDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            applicationContext,
                            "New Tag Adding is not success",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.w("TAG", "Error Adding tag", e)
                        alertDialog.dismiss()
                    }
            } else {
                //show warning
            }
        }
        alertDialog.show()
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.data != null
        ) {
            filePath = data.data!!
            uploadImage()
        }
    }

    private fun addMedia(url: String) {
        if (url.isEmpty()) {
            Toast.makeText(applicationContext, "Failed uploading photo", Toast.LENGTH_SHORT).show()
        } else {
            hashtag.file = url
            hashtag.file_size = ""
        }
    }

    private fun uploadImage() {
        val ref: StorageReference =
            storageReference.child("image/" + UUID.randomUUID().toString())
        ref.putFile(filePath)
            .addOnSuccessListener { _ ->
                ref.downloadUrl.addOnSuccessListener { uri ->
                  addMedia(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Failed " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
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
}