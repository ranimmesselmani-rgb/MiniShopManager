package com.example.minishopmanager

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class FavorisActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: FavorisAdapter
    private lateinit var tvEmpty: TextView
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoris)

        db = DBHelper(this)
        tvEmpty = findViewById(R.id.tvEmptyFav)
        recycler = findViewById(R.id.recyclerFavoris)

        adapter = FavorisAdapter(
            onAddCart = { item ->
                db.ajouterProduit(item.nom, item.prix, item.image)
                Toast.makeText(this, "${item.nom} ajouté au panier", Toast.LENGTH_SHORT).show()
            },
            onRemove = { item -> db.supprimerFavori(item.id); refresh() }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }
        refresh()
    }

    private fun refresh() {
        val items = db.getFavoris()
        adapter.submitList(items)
        tvEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        recycler.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }
}