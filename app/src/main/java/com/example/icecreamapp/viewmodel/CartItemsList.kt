package com.example.icecreamapp.viewmodel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.icecreamapp.screens.CartItemRow

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