import 'package:flutter/material.dart';
import 'added_list.dart';

class AddedProductsPage extends StatefulWidget {
  const AddedProductsPage({super.key});

  @override
  State<AddedProductsPage> createState() => _AddedProductsPageState();
}

class _AddedProductsPageState extends State<AddedProductsPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Produits ajoutés")),
      body: Column(
        children: [
          Expanded(
            child: addedProducts.isEmpty
                ? const Center(child: Text("Aucun produit ajouté pour le moment"))
                : ListView.builder(
                    itemCount: addedProducts.length,
                    itemBuilder: (context, index) {
                      final p = addedProducts[index];
                      return ListTile(
                        leading: const Icon(Icons.check_circle, color: Colors.green),
                        title: Text(p.name),
                        subtitle: Text("${p.price} DT"),
                      );
                    },
                  ),
          ),
          ElevatedButton(
            onPressed: () {
              setState(() {
                addedProducts.clear();
              });
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text("Liste vidée")),
              );
            },
            child: const Text("Vider la liste"),
          ),
          const SizedBox(height: 10),
        ],
      ),
    );
  }
}
