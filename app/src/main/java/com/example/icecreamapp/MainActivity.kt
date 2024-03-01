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

    fun addToCart(item: CartItem) {
        _cartItems.add(item)
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
        val subtotal = _cartItems.sumOf { it.cost * it.quantity }
        val discountAmount = subtotal * _couponDiscount.value
        _totalCost.value = subtotal - discountAmount
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IcecreamappTheme {
                IceCreamShopScreen()
            }
        }
    }
}

@Composable
fun IceCreamShopScreen(viewModel: IceCreamViewModel = viewModel()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        IceCreamAppBar()
        IceCreamSelectionUI(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamAppBar() {
    Surface {
        TopAppBar(
            title = {
                Text(
                    text = "Ice Cream Shop",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            modifier = Modifier
                .height(60.dp)
                .background(color = Color(0xFFE1BEE7))
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
        items.forEach { label ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                    selectedItem = label
                    expanded = false
                }
            )
        }
    }

    // Flavor selection with LazyColumn
    Text("Select Flavor:", Modifier.padding(top = 8.dp))
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .background(Color.White) // Background color for the border
            .border(1.dp, Color.Cyan, shape = RoundedCornerShape(8.dp))
            // Border color and width
            .padding(4.dp) // Padding inside the border
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(flavors) { flavor ->
                val isSelected = flavor == selectedFlavor
                val backgroundColor = if (isSelected) Color.LightGray else Color.Transparent // Change the background color if item is selected

                Text(
                    text = flavor,
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
                    Toast.makeText(context, "Coupon Applied!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Invalid Coupon", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.weight(0.3f)
        ) {
            Text("Apply")
        }


    }
    if (couponDiscount > 0) {
        Text(
            text = "Discount Applied Successfully",
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
    // Add to Cart Button
    Button(
        onClick = {
            val cost = if (selectedItem == "Cup") costPerCup else costPerCone
            viewModel.addToCart(CartItem(selectedItem, selectedFlavor, quantity, cost))
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
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
    val totalCost by viewModel.totalCost

    Text("Cart Items:", Modifier.padding(8.dp))

    val cornerShape = RoundedCornerShape(8.dp) // Adjust the corner radius as per your preference

    // Specify the maximum height for the LazyColumn
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp) // Set the max height to your preference
            .clip(cornerShape) // Apply the rounded corners to the box
            .background(Color.LightGray) // Set the background color
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(cartItems) { index, item ->
                CartItemRow(cartItem = item, onRemove = { viewModel.removeFromCart(index) })
            }
        }
    }

    Text("Total Cost: $${String.format("%.2f", totalCost)}",
        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(8.dp))
}
@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("${cartItem.type} - ${cartItem.flavor} x${cartItem.quantity}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    IcecreamappTheme {
        IceCreamShopScreen()
    }
}