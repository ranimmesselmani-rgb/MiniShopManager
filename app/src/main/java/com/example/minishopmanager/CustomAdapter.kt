package com.example.minishopmanager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

class CustomAdapter(
    private val context: Context,
    private val products: Array<String>,
    private val images: IntArray
) : BaseAdapter() {

    override fun getCount(): Int = products.size

    override fun getItem(position: Int): Any = products[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        // Main layout
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER_VERTICAL
        layout.setPadding(20, 20, 20, 20)

        // Product image
        val imageView = ImageView(context)
        imageView.setImageResource(images[position])
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        val imageSize =
            (130 * context.resources.displayMetrics.density).toInt()

        val imageParams = LinearLayout.LayoutParams(
            imageSize,
            imageSize
        )

        imageParams.setMargins(0, 0, 24, 0)
        layout.addView(imageView, imageParams)

        // Product name
        val textView = TextView(context)
        textView.text = products[position]
        textView.textSize = 22f
        textView.setTextColor(Color.BLACK)
        textView.setTypeface(null, Typeface.BOLD)

        layout.addView(
            textView,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        return layout
    }
}
