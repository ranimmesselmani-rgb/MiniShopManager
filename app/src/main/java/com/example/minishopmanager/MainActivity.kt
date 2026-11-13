package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var adapter: ProductAdapter
    private lateinit var btnPanier: MaterialButton

    private var allProducts = listOf<Product>()
    private var selectedCategory = "Tous"
    private var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DBHelper(this)
        btnPanier = findViewById(R.id.btnPanier)

        val prefs = getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)
        val user = prefs.getString("USERNAME", null)
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val salut = if (hour < 18) "Bonjour" else "Bonsoir"
        if (!user.isNullOrBlank()) {
            findViewById<TextView>(R.id.tvSubtitle).text = "$salut 👋"
            findViewById<TextView>(R.id.tvWelcome).text = user
        }

        adapter = ProductAdapter(
            onAdd = { produit ->
                if (db.ajouterProduit(produit.nom, produit.prix, produit.image)) {
                    updateCartCount()
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "${produit.nom} ajouté ✅",
                        Snackbar.LENGTH_LONG
                    ).setAction("Voir panier") {
                        startActivity(Intent(this, PanierActivity::class.java))
                    }.show()
                }
            },
            onOpen = { p ->
                val i = Intent(this, ProductDetailActivity::class.java)
                i.putExtra("nom", p.nom)
                i.putExtra("prix", p.prix)
                i.putExtra("categorie", p.categorie)
                i.putExtra("description", p.description)
                i.putExtra("icone", p.icone)
                i.putExtra("image", p.image)
                startActivity(i)
            },
            isFav = { p -> db.estFavori(p.nom) },
            onFav = { p ->
                val estFavori = db.basculerFavori(p.nom, p.prix, p.image)
                Toast.makeText(
                    this,
                    if (estFavori) "${p.nom} ajouté aux favoris ❤️" else "${p.nom} retiré des favoris",
                    Toast.LENGTH_SHORT
                ).show()
                estFavori
            }
        )

        val recycler = findViewById<RecyclerView>(R.id.recyclerProduits)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        allProducts = lireProduitsDepuisJSON()
        setupChips()
        applyFilter()

        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                query = s.toString().trim()
                applyFilter()
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        btnPanier.setOnClickListener {
            startActivity(Intent(this, PanierActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnProfil).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartCount()
        // تحديث القلوب بعد الرجوع من Mes favoris
        if (::adapter.isInitialized) adapter.notifyDataSetChanged()
    }

    private fun updateCartCount() {
        btnPanier.text = "🛒  Voir mon panier (${db.getNombreArticles()})"
    }

    private fun setupChips() {
        val group = findViewById<ChipGroup>(R.id.chipGroup)
        group.removeAllViews()
        val categories = listOf("Tous") + allProducts.map { it.categorie }.distinct()
        categories.forEach { cat ->
            val chip = Chip(this).apply {
                text = cat
                isCheckable = true
                isChecked = (cat == "Tous")
                setOnClickListener {
                    selectedCategory = cat
                    applyFilter()
                }
            }
            group.addView(chip)
        }
    }

    private fun applyFilter() {
        val filtered = allProducts.filter {
            (selectedCategory == "Tous" || it.categorie == selectedCategory) &&
                    it.nom.contains(query, ignoreCase = true)
        }
        adapter.submitList(filtered)
        findViewById<TextView>(R.id.tvCount).text = "${filtered.size} produit(s)"
        findViewById<TextView>(R.id.tvNoResult).visibility =
            if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun lireProduitsDepuisJSON(): List<Product> {
        val list = mutableListOf<Product>()
        try {
            val json = assets.open("produits.json").bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("produits")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    Product(
                        o.getInt("id"),
                        o.getString("nom"),
                        o.getDouble("prix"),
                        o.getString("categorie"),
                        o.optString("description", ""),
                        o.optString("icone", "🛍️"),
                        o.optString("image", "")
                    )
                )
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erreur de lecture du JSON", Toast.LENGTH_LONG).show()
        }
        return list
    }
}