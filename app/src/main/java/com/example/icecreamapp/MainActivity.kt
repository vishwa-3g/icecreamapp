package com.example.icecreamapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.icecreamapp.database.AppDatabase
import com.example.icecreamapp.screens.CartSummary
import com.example.icecreamapp.screens.IceCreamAppBar
import com.example.icecreamapp.screens.IceCreamSelectionUI
import com.example.icecreamapp.screens.OrdersList
import com.example.icecreamapp.ui.theme.IcecreamappTheme
import com.example.icecreamapp.viewmodel.IceCreamViewModel


//db instance to be used for operations
lateinit var db: AppDatabase

//main app entry function
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





