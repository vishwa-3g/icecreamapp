package com.example.icecreamapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.icecreamapp.database.placeOrder
import com.example.icecreamapp.viewmodel.IceCreamViewModel

//cart-summary ui to be shown
@Composable
fun CartSummary(viewModel: IceCreamViewModel, navController: NavController) {
    val totalCost by viewModel.totalCost
    val couponDiscount = viewModel.couponDiscount.value
    val originalTotal = viewModel.cartItems.sumOf { it.cost }
    val discountAmount = originalTotal * couponDiscount // Reference to discount amount
    val context = LocalContext.current
    val cartItems = viewModel.cartItems  // Reference to cart items

    //only shown if discount is applied
    Column(modifier = Modifier.padding(8.dp)) {
        if (couponDiscount > 0) {
            Text(
                text = "  Discount: -$${String.format("%.2f", discountAmount)}",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color= Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        //total cost shown
        Text(
            text = "Total Cost: $${String.format("%.2f", totalCost)}",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(8.dp)
        )
    }
    //place order button code and functionality
    Row(modifier = Modifier.padding(8.dp)) {
        Box {
            if (totalCost != 0.00) {
                Button(
                    onClick = {
                        val itemsCount = cartItems.sumOf { it.quantity }
                        placeOrder(itemsCount, totalCost)
                        viewModel.clearCart()
                        Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Place Order ➕")
                }
            }
        }
        //to view order history screen (navigation added!)
        Button(onClick = { navController.navigate("orderHistory") }) {
            Text("\uD83D\uDCCB View Order History")
        }
    }
}
