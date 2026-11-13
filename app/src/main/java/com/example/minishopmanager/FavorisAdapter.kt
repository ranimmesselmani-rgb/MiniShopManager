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

class FavorisAdapter(
    private val onAddCart: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : ListAdapter<CartItem, FavorisAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(a: CartItem, b: CartItem) = a.id == b.id
        override fun areContentsTheSame(a: CartItem, b: CartItem) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgFav)
        val nom: TextView = v.findViewById(R.id.tvFavNom)
        val prix: TextView = v.findViewById(R.id.tvFavPrix)
        val ajout: MaterialButton = v.findViewById(R.id.btnFavAjout)
        val suppr: MaterialButton = v.findViewById(R.id.btnFavSuppr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_favori, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)
        val ctx = h.itemView.context
        val resId = ctx.resources.getIdentifier(item.image, "drawable", ctx.packageName)
        h.img.setImageResource(if (resId != 0) resId else R.drawable.logo)

        h.nom.text = item.nom
        h.prix.text = "%.2f DT".format(item.prix)
        h.ajout.setOnClickListener { onAddCart(item) }
        h.suppr.setOnClickListener { onRemove(item) }
    }
}