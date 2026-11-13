package com.example.minishopmanager

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class PanierActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: CartAdapter
    private lateinit var tvTotal: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panier)

        db = DBHelper(this)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmpty = findViewById(R.id.tvEmpty)
        recycler = findViewById(R.id.recyclerPanier)

        adapter = CartAdapter(
            onQty = { item, delta -> db.changerQuantite(item.id, delta); refresh() },
            onDelete = { item -> db.supprimer(item.id); refresh() }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnVider).setOnClickListener {
            if (db.getNombreArticles() == 0) return@setOnClickListener
            AlertDialog.Builder(this)
                .setTitle("Vider le panier")
                .setMessage("Supprimer tous les articles ?")
                .setPositiveButton("Oui") { _, _ -> db.viderPanier(); refresh() }
                .setNegativeButton("Annuler", null)
                .show()
        }

        findViewById<MaterialButton>(R.id.btnCommander).setOnClickListener {
            if (db.getNombreArticles() == 0) {
                Toast.makeText(this, "Panier vide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Confirmer la commande")
                .setMessage("Total : %.2f DT".format(db.getTotal()))
                .setPositiveButton("Confirmer") { _, _ ->
                    db.enregistrerCommande()   // الجديد: تسجيل الطلبية
                    db.viderPanier()
                    refresh()
                    Toast.makeText(this, "Commande confirmée 🎉", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        refresh()
    }

    private fun refresh() {
        val items = db.getPanier()
        adapter.submitList(items)
        tvTotal.text = "%.2f DT".format(db.getTotal())
        tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }
}