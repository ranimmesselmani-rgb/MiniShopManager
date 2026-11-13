package com.example.minishopmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CommandeAdapter(
    private val onProduitClick: (CartItem) -> Unit
) : ListAdapter<Commande, CommandeAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Commande>() {
        override fun areItemsTheSame(a: Commande, b: Commande) = a.id == b.id
        override fun areContentsTheSame(a: Commande, b: Commande) = a == b
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val numero: TextView = v.findViewById(R.id.tvCmdNumero)
        val date: TextView = v.findViewById(R.id.tvCmdDate)
        val total: TextView = v.findViewById(R.id.tvCmdTotal)
        val lignes: LinearLayout = v.findViewById(R.id.containerLignes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_commande, parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val cmd = getItem(position)
        h.numero.text = "Commande #${cmd.id}"
        h.date.text = cmd.date
        h.total.text = "%.2f DT".format(cmd.total)

        h.lignes.removeAllViews()
        val inflater = LayoutInflater.from(h.itemView.context)
        val ctx = h.itemView.context

        cmd.items.forEach { item ->
            val ligne = inflater.inflate(R.layout.item_commande_ligne, h.lignes, false)

            val resId = ctx.resources.getIdentifier(item.image, "drawable", ctx.packageName)
            ligne.findViewById<ImageView>(R.id.imgLigne)
                .setImageResource(if (resId != 0) resId else R.drawable.logo)

            ligne.findViewById<TextView>(R.id.tvLigneNom).text = item.nom
            ligne.findViewById<TextView>(R.id.tvLigneDetail).text =
                "%.2f DT  ×  %d".format(item.prix, item.quantite)
            ligne.findViewById<TextView>(R.id.tvLigneSousTotal).text =
                "%.2f DT".format(item.prix * item.quantite)

            ligne.setOnClickListener { onProduitClick(item) }
            h.lignes.addView(ligne)
        }
    }
}