package com.example.minishopmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ProductAdapter(
    private val onAdd: (Product) -> Unit,
    private val onOpen: (Product) -> Unit,
    private val isFav: (Product) -> Boolean,
    private val onFav: (Product) -> Boolean
) : ListAdapter<Product, ProductAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(a: Product, b: Product) = a.id == b.id
        override fun areContentsTheSame(a: Product, b: Product) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduit)
        val nom: TextView = v.findViewById(R.id.tvNom)
        val desc: TextView = v.findViewById(R.id.tvDescription)
        val prix: TextView = v.findViewById(R.id.tvPrix)
        val cat: TextView = v.findViewById(R.id.tvCategorie)
        val btn: MaterialButton = v.findViewById(R.id.btnAjouter)
        val fav: TextView = v.findViewById(R.id.btnFavori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val p = getItem(position)

        val resId = h.itemView.context.resources.getIdentifier(
            p.image, "drawable", h.itemView.context.packageName
        )
        if (resId != 0) h.img.setImageResource(resId)
        else h.img.setImageResource(R.drawable.logo)

        h.nom.text = p.nom
        h.desc.text = p.description
        h.prix.text = "%.2f DT".format(p.prix)
        h.cat.text = p.categorie
        h.btn.setOnClickListener { onAdd(p) }
        h.itemView.setOnClickListener { onOpen(p) }

        // القلب
        h.fav.text = if (isFav(p)) "❤️" else "🤍"
        h.fav.setOnClickListener {
            val maintenantFavori = onFav(p)
            h.fav.text = if (maintenantFavori) "❤️" else "🤍"
        }

        h.itemView.alpha = 0f
        h.itemView.translationY = 40f
        h.itemView.animate().alpha(1f).translationY(0f)
            .setDuration(300)
            .setStartDelay((position * 50L).coerceAtMost(300))
            .start()
    }
}