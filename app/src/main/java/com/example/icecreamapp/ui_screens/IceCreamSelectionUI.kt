package com.example.icecreamapp.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.icecreamapp.model.CartItem
import com.example.icecreamapp.viewmodel.CartItemsList
import com.example.icecreamapp.viewmodel.IceCreamViewModel
import com.example.icecreamapp.model.flavorPricing

//main ui function starts here
@Composable
fun IceCreamSelectionUI(viewModel: IceCreamViewModel) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Cone") }
    var expanded by remember { mutableStateOf(false) }
    var selectedFlavor by remember { mutableStateOf("Mango") }
    val flavors = listOf("Mango", "Strawberry", "Chocolate", "Butterscotch", "Vanilla", "Banana", "Pistachio", "Raspberry", "Lemon", "Coffee", "Caramel", "Almond")
    var quantity by remember { mutableStateOf(1) }
    val costPerCup = 3.39
    val costPerCone = 3.69
    var couponCode by remember { mutableStateOf("") }
    val couponDiscount = viewModel.couponDiscount.value

    // Type selection dropdown
    OutlinedTextField(
        value = selectedItem,
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
            },
        label = { Text("Select Type") },
        trailingIcon = {
            Icon(if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, "contentDescription",
                Modifier.clickable { expanded = !expanded })
        }
    )
    //dropdown for cup and cone
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
    ) {
        DropdownMenuItem(
            text = { Text("Cup - $${String.format("%.2f", costPerCup)}") },
            onClick = {
                selectedItem = "Cup"
                expanded = false
            }
        )
        DropdownMenuItem(
            text = { Text("Cone - $${String.format("%.2f", costPerCone)}") },
            onClick = {
                selectedItem = "Cone"
                expanded = false
            }
        )
    }
    // Flavor selection with LazyColumn implementation
    Text("\uD83C\uDF68 Select Flavor: \uD83C\uDF67", Modifier
        .padding(top = 8.dp)
        .padding(bottom = 8.dp))
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        //lazy column with scroll
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(flavors) { flavor ->
                val isSelected = flavor == selectedFlavor
                val backgroundColor = if (isSelected) Color.Gray else Color.Transparent // Change the background color if item is selected
                val flavorPrice = flavorPricing[flavor] ?: 0.0 // Get flavor price, defaulting to 0.0 if not found

                Text(
                    text = "$flavor - $${String.format("%.2f", flavorPrice)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedFlavor = flavor
                        }
                        .padding(8.dp)
                        .background(backgroundColor)
                        .clip(if (isSelected) RoundedCornerShape(8.dp) else RoundedCornerShape(0.dp)) // Apply rounded corners if selected
                )
            }
        }
    }

    // Quantity selection with + and - functions
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Button(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
        Text("$quantity", Modifier.padding(horizontal = 8.dp))
        Button(onClick = { quantity++ }) { Text("+") }
    }

    //coupon code ui with apply button
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = couponCode,
            onValueChange = { couponCode = it },
            label = { Text("Coupon Code") },
            singleLine = true,
            modifier = Modifier.weight(0.7f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        //apply code button
        Button(
            onClick = {
                viewModel.applyCoupon(couponCode)
                if (viewModel.couponDiscount.value > 0) {
                    Toast.makeText(context, "Coupon Applied ✔", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Invalid Coupon ❌", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.weight(0.3f)
        ) {
            Text("Apply")
        }
    }
    //UI to notify user that discount has applied successfully
    if (couponDiscount > 0) {
        Text(
            text = "\uD83C\uDF89 Discount Applied Successfully! \uD83C\uDF89",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    // Add to Cart Button implementation
    Button(
        onClick = {
            val cost = if (selectedItem == "Cup") costPerCup else costPerCone
            viewModel.addToCart(CartItem(selectedItem, selectedFlavor, quantity, cost))
        },
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text("Add to Cart")
    }
    CartItemsList(viewModel)
}