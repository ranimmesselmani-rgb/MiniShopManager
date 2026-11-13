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

class CartAdapter(
    private val onQty: (CartItem, Int) -> Unit,
    private val onDelete: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(a: CartItem, b: CartItem) = a.id == b.id
        override fun areContentsTheSame(a: CartItem, b: CartItem) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgCart)
        val nom: TextView = v.findViewById(R.id.tvNom)
        val sousTotal: TextView = v.findViewById(R.id.tvSousTotal)
        val qte: TextView = v.findViewById(R.id.tvQte)
        val plus: MaterialButton = v.findViewById(R.id.btnPlus)
        val moins: MaterialButton = v.findViewById(R.id.btnMoins)
        val suppr: MaterialButton = v.findViewById(R.id.btnSuppr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)

        val ctx = h.itemView.context
        val resId = ctx.resources.getIdentifier(item.image, "drawable", ctx.packageName)
        h.img.setImageResource(if (resId != 0) resId else R.drawable.logo)

        h.nom.text = item.nom
        h.qte.text = item.quantite.toString()
        h.sousTotal.text = "%.2f DT".format(item.prix * item.quantite)
        h.plus.setOnClickListener { onQty(item, +1) }
        h.moins.setOnClickListener { onQty(item, -1) }
        h.suppr.setOnClickListener { onDelete(item) }
    }
}