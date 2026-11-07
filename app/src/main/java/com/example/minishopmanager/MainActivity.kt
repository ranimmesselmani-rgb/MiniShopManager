package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: Activity created")

        // Bouton Suivant
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            Toast.makeText(
                this,
                "Bonjour Ranim Meslmeni !",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Liste des produits
        val listView = findViewById<ListView>(R.id.listViewProducts)

        val products = arrayOf(
            "Téléphone",
            "Casque Bluetooth",
            "Montre connectée",
            "Chargeur USB",
            "Accessoires",
            "Cosmétiques"
        )

        // Images des produits
        val images = intArrayOf(
            R.drawable.phone,
            R.drawable.headphones,
            R.drawable.watch,
            R.drawable.charger,
            R.drawable.accessories,
            R.drawable.cosmetics
        )

        // Custom Adapter
        val adapter = CustomAdapter(
            this,
            products,
            images
        )

        listView.adapter = adapter

        // Gestion du clic sur un produit
        listView.setOnItemClickListener { parent, view, position, id ->

            val product = products[position]

            Toast.makeText(
                this,
                "Produit sélectionné : $product",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity started")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity stopped")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Activity destroyed")
        super.onDestroy()
    }
}
