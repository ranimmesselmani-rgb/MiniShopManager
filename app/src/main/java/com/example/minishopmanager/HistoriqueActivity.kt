package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.json.JSONObject

class HistoriqueActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: CommandeAdapter
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historique)

        db = DBHelper(this)
        tvEmpty = findViewById(R.id.tvEmptyCmd)
        recycler = findViewById(R.id.recyclerCommandes)

        adapter = CommandeAdapter { item -> ouvrirProduit(item) }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        refresh()
    }

    private fun refresh() {
        val commandes = db.getCommandes()
        adapter.submitList(commandes)
        tvEmpty.visibility = if (commandes.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (commandes.isEmpty()) View.GONE else View.VISIBLE
    }

    /** Cherche le produit dans produits.json (par nom) et ouvre sa page détail */
    private fun ouvrirProduit(item: CartItem) {
        try {
            val json = assets.open("produits.json").bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("produits")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                if (o.getString("nom") == item.nom) {
                    val intent = Intent(this, ProductDetailActivity::class.java)
                    intent.putExtra("nom", o.getString("nom"))
                    intent.putExtra("prix", o.getDouble("prix"))
                    intent.putExtra("categorie", o.getString("categorie"))
                    intent.putExtra("description", o.optString("description", ""))
                    intent.putExtra("icone", o.optString("icone", "🛍️"))
                    intent.putExtra("image", o.optString("image", ""))
                    startActivity(intent)
                    return
                }
            }
            Toast.makeText(this, "Produit introuvable", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur de lecture du JSON", Toast.LENGTH_SHORT).show()
        }
    }
}