package com.example.minishopmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "minishop.db", null, 5) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE panier (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT,
                prix REAL,
                quantite INTEGER DEFAULT 1,
                image TEXT DEFAULT ''
            )
            """
        )
        creerTableFavoris(db)
        creerTablesCommandes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE panier ADD COLUMN quantite INTEGER DEFAULT 1")
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE panier ADD COLUMN image TEXT DEFAULT ''")
        }
        if (oldVersion < 4) {
            creerTableFavoris(db)
        }
        if (oldVersion < 5) {
            creerTablesCommandes(db)
        }
    }

    private fun creerTableFavoris(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favoris (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT UNIQUE,
                prix REAL,
                image TEXT DEFAULT ''
            )
            """
        )
    }

    private fun creerTablesCommandes(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS commandes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT,
                total REAL
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS commande_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                commande_id INTEGER,
                nom TEXT,
                prix REAL,
                quantite INTEGER,
                image TEXT DEFAULT ''
            )
            """
        )
    }

    // ---------- PANIER ----------

    fun ajouterProduit(nom: String, prix: Double, image: String = ""): Boolean {
        val db = writableDatabase
        val c = db.rawQuery("SELECT id, quantite FROM panier WHERE nom = ?", arrayOf(nom))
        val ok = if (c.moveToFirst()) {
            val id = c.getInt(0)
            val q = c.getInt(1)
            val v = ContentValues().apply { put("quantite", q + 1) }
            db.update("panier", v, "id = ?", arrayOf(id.toString())) > 0
        } else {
            val v = ContentValues().apply {
                put("nom", nom)
                put("prix", prix)
                put("quantite", 1)
                put("image", image)
            }
            db.insert("panier", null, v) != -1L
        }
        c.close()
        return ok
    }

    fun changerQuantite(id: Int, delta: Int) {
        val db = writableDatabase
        db.execSQL("UPDATE panier SET quantite = quantite + ? WHERE id = ?", arrayOf(delta, id))
        db.delete("panier", "quantite <= 0", null)
    }

    fun supprimer(id: Int) {
        writableDatabase.delete("panier", "id = ?", arrayOf(id.toString()))
    }

    fun viderPanier() {
        writableDatabase.delete("panier", null, null)
    }

    fun getPanier(): List<CartItem> {
        val list = mutableListOf<CartItem>()
        val c = readableDatabase.rawQuery(
            "SELECT id, nom, prix, quantite, COALESCE(image,'') FROM panier", null
        )
        while (c.moveToNext()) {
            list.add(CartItem(c.getInt(0), c.getString(1), c.getDouble(2), c.getInt(3), c.getString(4)))
        }
        c.close()
        return list
    }

    fun getNombreArticles(): Int {
        val c = readableDatabase.rawQuery("SELECT COALESCE(SUM(quantite),0) FROM panier", null)
        c.moveToFirst()
        val n = c.getInt(0)
        c.close()
        return n
    }

    fun getTotal(): Double {
        val c = readableDatabase.rawQuery("SELECT COALESCE(SUM(prix*quantite),0) FROM panier", null)
        c.moveToFirst()
        val t = c.getDouble(0)
        c.close()
        return t
    }

    // ---------- FAVORIS ----------

    fun estFavori(nom: String): Boolean {
        val c = readableDatabase.rawQuery("SELECT 1 FROM favoris WHERE nom = ?", arrayOf(nom))
        val r = c.moveToFirst()
        c.close()
        return r
    }

    fun basculerFavori(nom: String, prix: Double, image: String): Boolean {
        return if (estFavori(nom)) {
            writableDatabase.delete("favoris", "nom = ?", arrayOf(nom))
            false
        } else {
            val v = ContentValues().apply {
                put("nom", nom)
                put("prix", prix)
                put("image", image)
            }
            writableDatabase.insert("favoris", null, v)
            true
        }
    }

    fun getFavoris(): List<CartItem> {
        val list = mutableListOf<CartItem>()
        val c = readableDatabase.rawQuery(
            "SELECT id, nom, prix, COALESCE(image,'') FROM favoris ORDER BY id DESC", null
        )
        while (c.moveToNext()) {
            list.add(CartItem(c.getInt(0), c.getString(1), c.getDouble(2), 1, c.getString(3)))
        }
        c.close()
        return list
    }

    fun supprimerFavori(id: Int) {
        writableDatabase.delete("favoris", "id = ?", arrayOf(id.toString()))
    }

    fun getNombreFavoris(): Int {
        val c = readableDatabase.rawQuery("SELECT COUNT(*) FROM favoris", null)
        c.moveToFirst()
        val n = c.getInt(0)
        c.close()
        return n
    }

    // ---------- COMMANDES ----------

    /** Copie le panier actuel dans l'historique. À appeler AVANT viderPanier() */
    fun enregistrerCommande(): Boolean {
        val items = getPanier()
        if (items.isEmpty()) return false
        val total = items.sumOf { it.prix * it.quantite }
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put("date", date)
                put("total", total)
            }
            val commandeId = db.insert("commandes", null, cv)
            items.forEach { it0 ->
                val v = ContentValues().apply {
                    put("commande_id", commandeId)
                    put("nom", it0.nom)
                    put("prix", it0.prix)
                    put("quantite", it0.quantite)
                    put("image", it0.image)
                }
                db.insert("commande_items", null, v)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return true
    }

    fun getCommandes(): List<Commande> {
        val list = mutableListOf<Commande>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT id, date, total FROM commandes ORDER BY id DESC", null)
        while (c.moveToNext()) {
            val id = c.getInt(0)
            val items = mutableListOf<CartItem>()
            val ci = db.rawQuery(
                "SELECT id, nom, prix, quantite, COALESCE(image,'') FROM commande_items WHERE commande_id = ?",
                arrayOf(id.toString())
            )
            while (ci.moveToNext()) {
                items.add(CartItem(ci.getInt(0), ci.getString(1), ci.getDouble(2), ci.getInt(3), ci.getString(4)))
            }
            ci.close()
            list.add(Commande(id, c.getString(1), c.getDouble(2), items))
        }
        c.close()
        return list
    }
}


