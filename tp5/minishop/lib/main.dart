import 'package:flutter/material.dart';
import 'catalogue_page.dart';

void main() {
  runApp(const MiniShopApp());
}

class MiniShopApp extends StatelessWidget {
  const MiniShopApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: "MiniShop Manager",
      theme: ThemeData(colorSchemeSeed: Colors.teal, useMaterial3: true),
      home: const CataloguePage(),
    );
  }
}
