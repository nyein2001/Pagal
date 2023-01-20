package com.service.mediataggingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.service.mediataggingapp.R
import com.service.mediataggingapp.model.ProjectInfo

class ProjectAdapter(private var context: Context, private var project: List<ProjectInfo>) :
    RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    lateinit var clickListener: ProjectClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return project.size
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val projectItem = project[position]
        holder.projectName.text = projectItem.name
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        OnClickListener {

        @BindView(R.id.project_item)
        lateinit var item: CardView

        @BindView(R.id.project_name)
        lateinit var projectName: TextView

        init {
            ButterKnife.bind(this, itemView)
            item.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onClick(v!!, adapterPosition)
        }
    }

    fun setOnItemClickListener(clickListener: ProjectClickListener) {
        this.clickListener = clickListener
    }

    interface ProjectClickListener {
        fun onClick(view: View, position: Int)
    }

}