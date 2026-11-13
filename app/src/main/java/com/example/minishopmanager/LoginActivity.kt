package com.example.minishopmanager

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etStudentName = findViewById<EditText>(R.id.etStudentName)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Remember Me (TP3)
        prefs = getSharedPreferences("CoursPrefs", MODE_PRIVATE)
        etStudentName.setText(prefs.getString("STUDENT", ""))
        cbRemember.isChecked = prefs.getBoolean("REMEMBER", false)

        btnLogin.setOnClickListener {

            // ✅ Vérification : nom non vide
            val name = etStudentName.text.toString().trim()
            if (name.isEmpty()) {
                etStudentName.error = "Nom obligatoire"
                return@setOnClickListener
            }

            // Remember Me
            val editor = prefs.edit()
            if (cbRemember.isChecked) {
                editor.putString("STUDENT", name)
                editor.putBoolean("REMEMBER", true)
            } else {
                editor.clear()
            }
            editor.apply()

            // ✅ Sauvegarde du nom pour l'afficher dans MainActivity
            getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)
                .edit()
                .putString("USERNAME", name)
                .apply()

            // Ouvrir l'écran principal
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
