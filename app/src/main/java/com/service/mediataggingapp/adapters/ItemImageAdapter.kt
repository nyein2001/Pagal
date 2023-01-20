package com.service.mediataggingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.balysv.materialripple.MaterialRippleLayout
import com.service.mediataggingapp.R
import com.service.mediataggingapp.utils.Tools

class ItemImageAdapter(private var context: Context, private var photos: MutableList<String>?): PagerAdapter() {

    override fun getCount(): Int {
        return this.photos!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = photos!![position]
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.image_item, container, false)
        val imageView : ImageView = view.findViewById(R.id.image) as ImageView
        Tools.displayImageOriginal(context, imageView, image)
        (container as ViewPager).addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as RelativeLayout)
    }
}