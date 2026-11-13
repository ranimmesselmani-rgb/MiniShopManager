package com.example.minishopmanager

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProductDetailActivity : AppCompatActivity() {

    private var qty = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val nom = intent.getStringExtra("nom") ?: ""
        val prix = intent.getDoubleExtra("prix", 0.0)
        val cat = intent.getStringExtra("categorie") ?: ""
        val desc = intent.getStringExtra("description") ?: ""
        val imgName = intent.getStringExtra("image") ?: ""

        val resId = resources.getIdentifier(imgName, "drawable", packageName)
        findViewById<ImageView>(R.id.imgBig).setImageResource(
            if (resId != 0) resId else R.drawable.logo
        )

        findViewById<TextView>(R.id.tvDNom).text = nom
        findViewById<TextView>(R.id.tvDCat).text = cat
        findViewById<TextView>(R.id.tvDDesc).text = desc
        findViewById<TextView>(R.id.tvDPrix).text = "%.2f DT".format(prix)

        val tvQty = findViewById<TextView>(R.id.tvQty)

        findViewById<MaterialButton>(R.id.btnPlus).setOnClickListener {
            qty++
            tvQty.text = qty.toString()
        }
        findViewById<MaterialButton>(R.id.btnMinus).setOnClickListener {
            if (qty > 1) {
                qty--
                tvQty.text = qty.toString()
            }
        }
        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnAddCart).setOnClickListener {
            val db = DBHelper(this)
            repeat(qty) { db.ajouterProduit(nom, prix, imgName) }
            Toast.makeText(this, "$qty × $nom ajouté au panier ✅", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}