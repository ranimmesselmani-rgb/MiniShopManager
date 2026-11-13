package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val prefs = getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)
        val user = prefs.getString("USERNAME", "Invité") ?: "Invité"

        findViewById<TextView>(R.id.tvAvatar).text =
            user.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        findViewById<TextView>(R.id.tvProfile).text = user

        val db = DBHelper(this)
        findViewById<TextView>(R.id.tvStatArticles).text = db.getNombreArticles().toString()
        findViewById<TextView>(R.id.tvStatTotal).text = "%.0f DT".format(db.getTotal())

        findViewById<MaterialButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnFavoris).setOnClickListener {
            startActivity(Intent(this, FavorisActivity::class.java))
        }

        // الجديد
        findViewById<MaterialButton>(R.id.btnCommandes).setOnClickListener {
            startActivity(Intent(this, HistoriqueActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            prefs.edit().remove("USERNAME").apply()
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
}