package com.example.icecreamapp.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.icecreamapp.database.Order
import com.example.icecreamapp.viewmodel.IceCreamViewModel

//UI with delete function to show for each order on order hsi-troy screen (called based on conditions and refreshed if any operations done)
@Composable
fun OrderItem(order: Order, viewModel: IceCreamViewModel, refreshOrders: MutableState<Boolean>) {
    //UI to show order details card on order history screen
    Card(
        modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order ID: ${order.id}", fontWeight = FontWeight.Bold)
            Text("Number of Items: ${order.items}")
            Text("Total Cost: $${String.format("%.2f", order.totalCost)}")

            //delete icon ui with code to trigger in DB once clicked to delete the specific order from DB
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    viewModel.deleteOrder(order) { refreshOrders.value = true } // Set refresh flag after deletion
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Order")
                }
            }
        }
    }
}