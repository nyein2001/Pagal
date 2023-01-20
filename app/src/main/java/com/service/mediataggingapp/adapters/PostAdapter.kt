package com.service.mediataggingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.service.mediataggingapp.R
import com.service.mediataggingapp.model.ItemInfo
import com.service.mediataggingapp.utils.Tools

class PostAdapter(private var context: Context, private var posts: List<ItemInfo>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent,false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        if (post.photos!!.size != 0) {
            Tools.displayImageOriginal(context, holder.icon, post.photos?.get(0) ?: "")
        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {

        @BindView(R.id.tag_item)
        lateinit var postItem: CardView

        @BindView(R.id.tag_icon)
        lateinit var icon: ImageView

        @BindView(R.id.tag_name)
        lateinit var name: TextView

        init {
            ButterKnife.bind(this, itemView)
        }

        override fun onClick(v: View?) {

        }
    }
}