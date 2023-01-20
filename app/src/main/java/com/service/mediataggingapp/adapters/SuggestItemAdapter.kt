package com.service.mediataggingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.service.mediataggingapp.R
import com.service.mediataggingapp.model.Hashtag
import com.service.mediataggingapp.utils.Tools

class SuggestItemAdapter : RecyclerView.Adapter<SuggestItemAdapter.ItemViewHolder>(), Filterable {

    private lateinit var context: Context
    private var hashtags: List<Hashtag> = ArrayList()
    private var hashtagsFiltered: List<Hashtag> = ArrayList()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.tag_suggest_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val hashtag = hashtagsFiltered[position]
        holder.tagName.text = hashtag.name
        Tools.displayImageOriginal(context, holder.tagIcon, hashtag.file.toString())
        holder.suggestItem.setOnClickListener { view ->
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, hashtag)
            }
        }
    }

    override fun getItemCount(): Int {
        return hashtagsFiltered.size
    }

    fun setHashTagList(context: Context?, hashtags: List<Hashtag>) {
        this.context = context!!
        this.hashtags = hashtags
        this.hashtagsFiltered = hashtags
        notifyItemChanged(0, hashtags.size)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this, itemView)
        }

        @BindView(R.id.tag_icon)
        lateinit var tagIcon: ImageView

        @BindView(R.id.tag_name)
        lateinit var tagName: TextView

        @BindView(R.id.suggest_item)
        lateinit var suggestItem: CardView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                hashtagsFiltered = if (charString.isEmpty() || charString == "#") {
                    hashtags
                } else {
                    val filterList: MutableList<Hashtag> = ArrayList()
                    for (hashtag: Hashtag in hashtags) {
                        if (hashtag.name!!.lowercase().contains(charString.lowercase())) {
                            filterList.add(hashtag)
                        }
                    }
                    filterList
                }
                val filterResults = FilterResults()
                filterResults.values = hashtagsFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                hashtagsFiltered = if (results?.values == null) {
                    ArrayList()
                } else
                    results.values as ArrayList<Hashtag>

                notifyDataSetChanged()
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, hashtag: Hashtag)
    }


}