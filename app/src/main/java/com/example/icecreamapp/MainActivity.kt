package com.example.icecreamapp

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.icecreamapp.ui.theme.IcecreamappTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//cart items list
data class CartItem(
    val type: String,
    val flavor: String,
    val quantity: Int,
    val cost: Double
)

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
//table name for DB declaration
@Entity(tableName = "orders") // Explicit table name
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "items") val items: Int,
    @ColumnInfo(name = "total_cost") val totalCost: Double
)

//query to interact with DB declaration
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAll(): List<Order>

    @Insert
    fun insertAll(vararg orders: Order)

    @Delete
    fun delete(order: Order)

    @Query("SELECT * FROM orders ORDER BY total_cost DESC LIMIT 10")
    fun getTopTenOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE total_cost > 50")
    fun getOrdersOverFifty(): List<Order>

}
//create assistance of db to interact with application from backed
@Database(entities = [Order::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
//db instance to be used for operations
public lateinit var db: AppDatabase
//main entry function
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //db instance builder to use in application
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "ice-cream-app-db"
        ).allowMainThreadQueries()
            .build()

        setContent {
            IcecreamappTheme {
                // Navigation setup
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "iceCreamShop") {
                    composable("iceCreamShop") { IceCreamShopScreen(navController = navController) }
                    composable("orderHistory") { OrderHistoryScreen(navController = navController) }
                }
            }
        }
    }
}

// Insert the new order into the database
fun placeOrder(items: Int, totalCost: Double) {
    val newOrder = Order(items = items, totalCost = totalCost)
    db.orderDao().insertAll(newOrder)
}
//main entry screen
@Composable
fun IceCreamShopScreen(navController: NavController, viewModel: IceCreamViewModel = viewModel()) {
    // This scroll state allows the entire column content to scroll if screen rotated and persist the values.
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState) // Allow vertical scrolling if screen rotation happens. reference:https://developer.android.com/
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IceCreamAppBar()
        IceCreamSelectionUI(viewModel)
        CartSummary(viewModel, navController)
    }
}
//top app bar to be shown
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
// show cart items added in viewmodel list
@Composable
fun CartItemsList(viewModel: IceCreamViewModel) {
    val cartItems = viewModel.cartItems

    Text(" \uD83D\uDED2 Cart Items:", Modifier.padding(8.dp))

    val cornerShape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 150.dp)
            .clip(cornerShape)
            .background(Color.LightGray)
    ) {
        LazyColumn {
            itemsIndexed(cartItems) { index, item ->
                CartItemRow(
                    cartItem = item,
                    //remove cart item from list
                    onRemove = { viewModel.removeFromCart(index) }
                )
            }
        }
    }
}
//cart item list to be shown
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
                color=Color.Red,
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
                        Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
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

//order history screen to show orders and delete if required
@Composable
fun OrderHistoryScreen(navController: NavController? = null, viewModel: IceCreamViewModel = viewModel()) {
    // Back arrow at the top left to go back on main screen (navigation added)
    IconButton(
        onClick = { navController?.popBackStack() },
        modifier = Modifier
            .padding(start = 16.dp) // Add top and start padding
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back"
        )
    }
    // Use viewModel to fetch orders from the database
    val ordersToShow by viewModel.ordersList
    // get all orders button and trigger function for DAO to fetch data from room and show it in viewmodel
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            viewModel.fetchAllOrdersAsync()
        }) {
            Text("Show All Orders")
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            // get top 10 orders button and trigger function for DAO to fetch data from room and show it in viewmodel
            Button(
                onClick = {
                    viewModel.fetchTopTenOrdersAsync() // Fetches top 10 orders
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("Show Top 10 Orders")
            }
            // get >50 cost orders button and trigger function for DAO to fetch data from room and show it in viewmodel
            Button(
                onClick = {
                    viewModel.fetchOrdersOverFiftyAsync() // Fetches orders over $50
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Show Orders > $50")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))

        if (ordersToShow.isNotEmpty()) {
            OrdersList(orders = ordersToShow, viewModel = viewModel) // Pass viewModel to OrdersList

        } else {
            Text(
                "No orders Found!",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp) // Adjust padding value as needed
                ,
                fontSize = 30.sp, // Adjust font size as needed
                fontWeight = FontWeight.Bold
            )

        }
    }
}
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



