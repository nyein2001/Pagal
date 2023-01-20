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
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.utils.Tools

class TagAdapter(private var context: Context, private var tags: List<Hashtag>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private lateinit var clickListener: TagClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tagItem = tags[position]
        holder.name.text = tagItem.name
        Tools.displayImageOriginal(context, holder.icon, tagItem.file!!)

    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnClickListener {

        @BindView(R.id.tag_item)
        lateinit var item: CardView

        @BindView(R.id.tag_icon)
        lateinit var icon: ImageView

        @BindView(R.id.tag_name)
        lateinit var name: TextView


        init {
            ButterKnife.bind(this, itemView)
            item.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onClick(v!!, adapterPosition)
        }
    }

    fun setOnItemClickListener(clickListener: TagClickListener) {
        this.clickListener = clickListener
    }

    interface TagClickListener {
        fun onClick(view: View, position: Int)
    }
}