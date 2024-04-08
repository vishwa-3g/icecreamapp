package com.example.icecreamapp.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.icecreamapp.database.Order
import com.example.icecreamapp.viewmodel.IceCreamViewModel

//function to show and list the orders based on conditions
@Composable
fun OrdersList(orders: List<Order>, viewModel: IceCreamViewModel) {
    val refreshOrders = remember { mutableStateOf(false) } // State variable to trigger refresh

    LaunchedEffect(refreshOrders.value) { // Trigger on refreshOrders change
        if (refreshOrders.value) {
            viewModel.fetchAllOrdersAsync() // Refresh orders
            refreshOrders.value = false // Reset refresh flag
        }
    }

    LazyColumn {
        items(orders, key = { order -> order.id }) { order ->
            OrderItem(order = order, viewModel = viewModel, refreshOrders = refreshOrders) // Pass refreshOrders to OrderItem
        }
    }
}