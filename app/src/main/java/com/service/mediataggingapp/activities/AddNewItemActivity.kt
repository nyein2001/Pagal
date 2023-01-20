package com.service.mediataggingapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
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
import com.service.mediataggingapp.adapters.ItemImageAdapter
import com.service.mediataggingapp.adapters.SuggestItemAdapter
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.utils.*
import com.service.mediataggingapp.view.TagView
import java.util.*
import kotlin.collections.ArrayList

class AddNewItemActivity : AppCompatActivity(), TagView {

    @BindView(R.id.material_toolbar)
    lateinit var toolbar: MaterialToolbar

    @BindView(R.id.media_view)
    lateinit var mediaView: ImageView

    @BindView(R.id.media_upload)
    lateinit var uploadBtn: TextView

    @BindView(R.id.item_image_gallery)
    lateinit var imageGallery: LinearLayout

    @BindView(R.id.pager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.layout_dots)
    lateinit var layoutDots: LinearLayout

    @BindView(R.id.bt_previous)
    lateinit var prevBtn: ImageButton

    @BindView(R.id.bt_next)
    lateinit var nextBtn: ImageButton

    @BindView(R.id.suggest_hashtag)
    lateinit var suggestItemRecyclerView: RecyclerView

    @BindView(R.id.edt_hashtag)
    lateinit var edtSearch: EditText

    @BindView(R.id.added_hashtag)
    lateinit var addedTagItemRecyclerView: RecyclerView

    private lateinit var presenter: Presenter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var itemInfo: ItemInfo
    private lateinit var mAdapter: ItemImageAdapter
    private lateinit var filePath: Uri
    private lateinit var suggestItemAdapter: SuggestItemAdapter
    private lateinit var addedHashtagAdapter: SuggestItemAdapter
    private lateinit var suggestTagList: MutableList<Hashtag>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val init = InitTheme(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_add_new_item)
        ButterKnife.bind(this)
        presenter = Presenter(this)
        initComponent()
        imagesUploadAction()
        initAddedTagRecyclerView()
        setSuggestionView()
        savePost()
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        itemInfo = ItemInfo()
        mAdapter = ItemImageAdapter(this, itemInfo.photos)
        suggestTagList = ArrayList()
    }

    private fun imagesUploadAction() {
        uploadBtn.setOnClickListener {
            chooseItemImages()
        }
    }

    private fun chooseItemImages() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            Constants.BundleKeys.PICK_ITEM_IMAGE_REQUEST
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.BundleKeys.PICK_ITEM_IMAGE_REQUEST
            && resultCode == RESULT_OK && data != null && data.data != null
        ) {
            filePath = data.data!!
            uploadImage()
        }
    }

    private fun addMedia(url: String) {
        if (url.isNotEmpty()) {
            itemInfo.photos?.add(url)
            mAdapter.notifyDataSetChanged()
            setMediaSlideView(itemInfo.photos)
        }
    }

    private fun uploadImage() {
        val ref: StorageReference =
            storageReference.child("images/" + UUID.randomUUID().toString())
        ref.putFile(filePath)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    addMedia(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Failed " + e.message, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun setMediaSlideView(photos: MutableList<String>?) {
        if (photos!!.isNotEmpty()) {
            mediaView.visibility = View.GONE
            imageGallery.visibility = View.VISIBLE
            displayResultData(photos)
        } else {
            mediaView.visibility = View.VISIBLE
            imageGallery.visibility = View.GONE
        }
    }

    private fun displayResultData(photos: MutableList<String>) {

        if (photos.size <= 1) {
            prevBtn.visibility = View.INVISIBLE
            nextBtn.visibility = View.INVISIBLE
            layoutDots.visibility = View.INVISIBLE
        } else {
            prevBtn.visibility = View.VISIBLE
            nextBtn.visibility = View.VISIBLE
            layoutDots.visibility = View.VISIBLE
        }

        viewPager.adapter = mAdapter

        val params = viewPager.layoutParams
        params.height = Tools.getImageHeight(this)
        viewPager.layoutParams = params
        viewPager.currentItem = 0
        addBottomDots(layoutDots, mAdapter.count, 0)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                addBottomDots(layoutDots, mAdapter.count, position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        prevBtn.setOnClickListener {
            prevAction()
        }
        nextBtn.setOnClickListener {
            nextAction()
        }
    }

    private fun addBottomDots(layout_dots: LinearLayout, size: Int, current: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        layout_dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val widthAndHeight = 10
            val params =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams(widthAndHeight, widthAndHeight))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            dots[i]!!.setImageResource(R.drawable.shape_circle)
            dots[i]!!.setColorFilter(ContextCompat.getColor(this, R.color.darkOverlaySoft))
            layout_dots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[current]!!.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryLight
                )
            )
        }
    }

    private fun prevAction() {
        var pos = viewPager.currentItem
        pos -= 1
        if (pos < 0) pos = mAdapter.count
        viewPager.currentItem = pos
    }

    private fun nextAction() {
        var pos = viewPager.currentItem
        pos += 1
        if (pos >= mAdapter.count) pos = 0
        viewPager.currentItem = pos
    }

    private fun initAddedTagRecyclerView() {
        addedTagItemRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        addedHashtagAdapter = SuggestItemAdapter()
        addedTagItemRecyclerView.adapter = addedHashtagAdapter
    }

    private fun setSuggestionView() {
        presenter.getTags()
        suggestItemRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        suggestItemAdapter = SuggestItemAdapter()
        suggestItemRecyclerView.adapter = suggestItemAdapter

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                tagName: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                suggestItemAdapter.filter.filter(tagName)
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        suggestItemAdapter.setOnItemClickListener(object : SuggestItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, hashtag: Hashtag) {
                itemInfo.tags!!.add(hashtag)
                mAdapter.notifyDataSetChanged()
                setAddedTagItemsView(itemInfo.tags)
            }

        })
    }

    private fun setAddedTagItemsView(tags: MutableList<Hashtag>?) {
        addedHashtagAdapter.setHashTagList(applicationContext, tags!!)
    }

    private fun savePost() {
        toolbar.setNavigationOnClickListener {
            if (itemInfo.photos != null && itemInfo.tags != null) {
                Log.d("TAG", "savePost() process is running")
                itemInfo.created_at = Calendar.getInstance().time.toString()
                itemInfo.last_modified_at = Calendar.getInstance().time.toString()
                Log.d("TAG", "Start Posting")
                db.collection("posts")
                    .add(itemInfo)
                    .addOnSuccessListener {
                        Log.d("TAG", "Successfully Added")
                        Toast.makeText(applicationContext, "Successfully Added", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(applicationContext, ItemActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.d("TAG", "Posting Failure")
                        Toast.makeText(applicationContext, "Posting Failure", Toast.LENGTH_SHORT).show()
                        Log.w("TAG", "Error Posting", e)
                    }
            } else {
                Log.d("TAG", "Please Fill the requirements")
                Toast.makeText(applicationContext, "Please Fill the requirements ", Toast.LENGTH_SHORT).show()
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

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun setProject(hashtags: List<Hashtag>) {
        suggestItemAdapter.setHashTagList(applicationContext, hashtags)
    }

    override fun onErrorLoading(message: String?) {
    }
}