package com.example.icecreamapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icecreamapp.ui.theme.IcecreamappTheme


data class CartItem(
    val type: String,
    val flavor: String,
    val quantity: Int,
    val cost: Double
)

class IceCreamViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems

    private var _couponDiscount = mutableStateOf(0.0)
    val couponDiscount: State<Double> = _couponDiscount

    private val _totalCost = mutableStateOf(0.0)
    val totalCost: State<Double> = _totalCost

    private val costPerCup = 3.39
    private val costPerCone = 3.69
    fun addToCart(item: CartItem) {
        val flavorPrice = flavorPricing[item.flavor] ?: 0.0 // Default to 0.0 if not found
        val baseCost = if (item.type == "Cup") costPerCup else costPerCone
        val totalItemCost = baseCost + flavorPrice
        val newItem = item.copy(cost = totalItemCost * item.quantity)
        _cartItems.add(newItem)
        calculateTotalCost()
    }

    fun removeFromCart(itemIndex: Int) {
        _cartItems.removeAt(itemIndex)
        calculateTotalCost()
    }
    fun applyCoupon(code: String) {
        if (code.isEmpty()) {
            // Reset discount if coupon code is erased
            _couponDiscount.value = 0.0
        } else if (code == "DISCOUNT10") {
            // Apply 10% discount for "DISCOUNT10"
            _couponDiscount.value = 0.1
        } else {
            // Reset discount if any other non-empty, invalid code is entered
            _couponDiscount.value = 0.0
        }
        calculateTotalCost()
    }

    private fun calculateTotalCost() {
        val subtotal = _cartItems.sumOf { it.cost }
        val discountAmount = subtotal * _couponDiscount.value
        _totalCost.value = subtotal - discountAmount
    }
}
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IcecreamappTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize() // Fill the available space
                        .background(color = Color(0xFFE6E6FA)) // Set the background color
                ) {
                    IceCreamShopScreen()
                }
            }

            }
        }
    }

@Composable
fun IceCreamShopScreen(viewModel: IceCreamViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        IceCreamAppBar()
        IceCreamSelectionUI(viewModel)
        CartSummary(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamAppBar() {
    Surface {
        TopAppBar(
            title = {
                Text(
                    text = "\uD83C\uDF66Ice Cream Shop\uD83C\uDF66",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            modifier = Modifier
                .height(50.dp)
                .background(color = Color(0xFFE6E6FA))
        )
    }
}

@Composable
fun IceCreamSelectionUI(viewModel: IceCreamViewModel) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf("Cone") }
    var expanded by remember { mutableStateOf(false) }
    val items = listOf("Cone", "Cup")
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
                // This is needed to align the DropdownMenu with the TextField
            },
        label = { Text("Select Type") },
        trailingIcon = {
            Icon(if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, "contentDescription",
                Modifier.clickable { expanded = !expanded })
        }
    )
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
    // Flavor selection with LazyColumn
    Text("\uD83C\uDF68 Select Flavor: \uD83C\uDF67", Modifier
        .padding(top = 8.dp)
        .padding(bottom = 8.dp))
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .background(Color.White) // Background color for the border
            .border(1.dp, Color.Blue, shape = RoundedCornerShape(8.dp))
            // Border color and width
            .padding(4.dp) // Padding inside the border
    ) {
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

    // Quantity selection
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
        Button(onClick = { if (quantity > 1) quantity-- }) { Text("-") }
        Text("$quantity", Modifier.padding(horizontal = 8.dp))
        Button(onClick = { quantity++ }) { Text("+") }
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = couponCode,
            onValueChange = { couponCode = it },
            label = { Text("Coupon Code") },
            singleLine = true,
            modifier = Modifier.weight(0.7f)
        )

        Spacer(modifier = Modifier.width(8.dp)) // Optional space between the text field and button

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
    if (couponDiscount > 0) {
        Text(
            text = "\uD83C\uDF89 Discount Applied Successfully! \uD83C\uDF89",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    // Add to Cart Button
    Button(
        onClick = {
            val cost = if (selectedItem == "Cup") costPerCup else costPerCone
            viewModel.addToCart(CartItem(selectedItem, selectedFlavor, quantity, cost))
            Toast.makeText(context, "Added to cart ✔", Toast.LENGTH_SHORT).show()
        },

        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text("Add to Cart")
    }
    CartItemsList(viewModel)
}
@Composable
fun CartItemsList(viewModel: IceCreamViewModel) {
    val cartItems = viewModel.cartItems


    Text(" \uD83D\uDED2 Cart Items:", Modifier.padding(8.dp))

    val cornerShape = RoundedCornerShape(8.dp) // Adjust the corner radius as per your preference

    // Specify the maximum height for the LazyColumn
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp) // Set the max height to your preference
            .clip(cornerShape) // Apply the rounded corners to the box
            .background(Color.LightGray) // Set the background color
    ) {
        LazyColumn {
            itemsIndexed(cartItems) { index, item ->
                CartItemRow(
                    cartItem = item,
                    onRemove = { viewModel.removeFromCart(index) }
                )
            }
        }
    }
}
@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${cartItem.type} - ${cartItem.flavor} x${cartItem.quantity} - $${String.format("%.2f", cartItem.cost)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}
@Composable
fun CartSummary(viewModel: IceCreamViewModel) {
    val totalCost by viewModel.totalCost
    val couponDiscount = viewModel.couponDiscount.value
    val originalTotal = viewModel.cartItems.sumOf { it.cost }
    val discountAmount = originalTotal * couponDiscount


    Column(modifier = Modifier.padding(8.dp)) {
        if (couponDiscount > 0) {
            Text(
                text = "   Discount: -$${String.format("%.2f", discountAmount)}",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color=Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Text(
            text = "Total Cost: $${String.format("%.2f", totalCost)}",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IcecreamappTheme {
        IceCreamShopScreen()
    }
}