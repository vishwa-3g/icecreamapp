package com.example.icecreamapp.model

// CartItem data class
data class CartItem(
    val type: String,
    val flavor: String,
    val quantity: Int,
    val cost: Double
)

//mapof function used from kotlin documentation ref: https://kotlinlang.org/docs/map-operations.html
val flavorPricing = mapOf(
    "Mango" to 3.5,
    "Strawberry" to 2.0,
    "Chocolate" to 3.5,
    "Butterscotch" to 2.5,
    "Vanilla" to 2.0,
    "Banana" to 2.5,
    "Pistachio" to 1.5,
    "Raspberry" to 1.5,
    "Lemon" to 2.0,
    "Coffee" to 2.5,
    "Caramel" to 1.5,
    "Almond" to 3.5
)
