import 'package:flutter/material.dart';
import 'product.dart';
import 'added_list.dart';
import 'added_products_page.dart';

final List<Product> catalogue = [
  Product("Téléphone", 1200),
  Product("Casque Bluetooth", 150),
  Product("Montre connectée", 300),
  Product("Chargeur USB", 40),
  Product("Clavier Sans Fil", 80),
];

class CataloguePage extends StatelessWidget {
  const CataloguePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Catalogue MiniShop"),
        actions: [
          IconButton(
            icon: const Icon(Icons.list),
            tooltip: "Produits ajoutés",
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const AddedProductsPage()),
              );
            },
          ),
        ],
      ),
      body: ListView.builder(
        itemCount: catalogue.length,
        itemBuilder: (context, index) {
          final p = catalogue[index];
          return ListTile(
            leading: const Icon(Icons.shopping_bag),
            title: Text(p.name),
            subtitle: Text("${p.price} DT"),
            trailing: const Icon(Icons.shopping_cart),
            onTap: () {
              addedProducts.add(p);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text("Produit ajouté : ${p.name}"),
                  duration: const Duration(seconds: 1),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
