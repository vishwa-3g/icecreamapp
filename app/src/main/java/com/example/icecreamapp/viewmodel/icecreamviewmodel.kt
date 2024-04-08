package com.example.icecreamapp.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.icecreamapp.database.AppDatabase
import com.example.icecreamapp.database.Order
import com.example.icecreamapp.model.CartItem
import com.example.icecreamapp.model.flavorPricing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//ice cream viewmodel for showing dynamic order list,functions and cart values
class IceCreamViewModel(application: Application) : AndroidViewModel(application) {
    // database instance builder
    private val db: AppDatabase = Room.databaseBuilder(application, AppDatabase::class.java, "ice-cream-app-db").build()
    //cart times list
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> = _cartItems
    //discount code implementation
    private var _couponDiscount = mutableStateOf(0.0)
    val couponDiscount: State<Double> = _couponDiscount
    //total-cost value
    private val _totalCost = mutableStateOf(0.0)
    val totalCost: State<Double> = _totalCost
    //cup cone dropdown value declaration
    private val costPerCup = 3.39
    private val costPerCone = 3.69

    //add to cart button functionality
    fun addToCart(item: CartItem) {
        val flavorPrice = flavorPricing[item.flavor] ?: 0.0 // Default to 0.0 if not found
        val baseCost = if (item.type == "Cup") costPerCup else costPerCone
        val totalItemCost = baseCost + flavorPrice
        val newItem = item.copy(cost = totalItemCost * item.quantity)
        _cartItems.add(newItem)
        calculateTotalCost()
    }

    // remove from cart button functionality
    fun removeFromCart(itemIndex: Int) {
        _cartItems.removeAt(itemIndex)
        calculateTotalCost()
    }

    //apply discount code functionality
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

    //for recalculate the total cost whenever item added or deleted
    private fun calculateTotalCost() {
        val subtotal = _cartItems.sumOf { it.cost }
        val discountAmount = subtotal * _couponDiscount.value
        _totalCost.value = subtotal - discountAmount
    }
    //clear cart once order placed
    fun clearCart() {
        _cartItems.clear()
        calculateTotalCost() // Recalculate the total cost which should now be 0
    }
    //to capture list of orders
    private val _ordersList = mutableStateOf(listOf<Order>())
    val ordersList: State<List<Order>> = _ordersList
    init {
        fetchAllOrdersAsync()
    }
    //async coroutine function to fetch orders and show on order history screen
    fun fetchAllOrdersAsync() = viewModelScope.launch {
        // Switch to IO dispatcher for database access
        val orders = withContext(Dispatchers.IO) {
            db.orderDao().getAll()
        }
        _ordersList.value = orders
    }
    //async coroutine function to fetch top 10 orders and show on order history screen
    fun fetchTopTenOrdersAsync() = viewModelScope.launch {
        val orders = withContext(Dispatchers.IO) {
            db.orderDao().getTopTenOrders()
        }
        _ordersList.value = orders
    }
    //async coroutine function to fetch more than 50 cost orders and show on order history screen
    fun fetchOrdersOverFiftyAsync() = viewModelScope.launch {
        val orders = withContext(Dispatchers.IO) {
            db.orderDao().getOrdersOverFifty()
        }
        _ordersList.value = orders
    }
    //execute , delete order function and revise order list function
    fun deleteOrder(order: Order, onDeleteCompleted: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.orderDao().delete(order)
            }
            onDeleteCompleted() // Call the callback after deletion
        }
    }

}
