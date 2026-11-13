package com.example.minishopmanager

data class Product(
    val id: Int,
    val nom: String,
    val prix: Double,
    val categorie: String,
    val description: String,
    val icone: String = "🛍️",
    val image: String = ""
)

data class CartItem(
    val id: Int,
    val nom: String,
    val prix: Double,
    val quantite: Int,
    val image: String = ""
)

data class Commande(
    val id: Int,
    val date: String,
    val total: Double,
    val items: List<CartItem>
)