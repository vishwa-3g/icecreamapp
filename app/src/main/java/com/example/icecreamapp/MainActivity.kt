package com.example.icecreamapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.icecreamapp.ui.theme.IcecreamappTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxWidth as fillMaxWidth1
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    private val isNegativeQuantity = mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IceCreamAppBar()
                dropDownMenu()


            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IceCreamAppBar() {
    Surface(
        color = Color.Red , // Corrected background color to light blue
        modifier = Modifier
            .fillMaxWidth1() // Corrected typo from fillMaxWidth1 to fillMaxWidth
            .height(60.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Ice Cream Bar",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 12.dp), // Added top padding to lower the text
                    textAlign = TextAlign.Center
                )
            },
            modifier = Modifier
                .fillMaxWidth1() // Corrected typo from fillMaxWidth1 to fillMaxWidth
                .height(60.dp)
        )
    }
}


@Composable
fun dropDownMenu() {
    var expanded by remember { mutableStateOf(false) }
    val list = listOf("Cone", "Cup")
    var selectedItem by remember { mutableStateOf("Cone") }
    var textFiledSize by remember { mutableStateOf(Size.Zero) }
    var quantity by remember { mutableStateOf(0) }
    val costPerCup = 3.39
    val costPerCone = 3.69
    val context = LocalContext.current

    val icon = if (expanded) {
        Icons.Default.KeyboardArrowUp
    } else {
        Icons.Default.KeyboardArrowDown
    }

    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            text = "Type:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = selectedItem,
            onValueChange = { selectedItem = it },
            modifier = Modifier
                .fillMaxWidth1()
                .onGloballyPositioned { coordinates ->
                    textFiledSize = coordinates.size.toSize()
                },
            label = { Text(text = "Select Variation") },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFiledSize.width.toDp() })
        ) {
            list.forEach { label ->
                DropdownMenuItem(
                    {
                        Text(text = label)
                    },
                    onClick = {
                        selectedItem = label
                        expanded = false
                    }
                )
            }
        }
        ImageCenter(selectedItem = selectedItem)
        Row(
            modifier = Modifier.fillMaxWidth1(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Decrease quantity
                    if (quantity > 0) {
                        quantity--
                    } else {
                        // Show Toast message for negative quantity
                        Toast.makeText(context, "Quantity cannot be negative!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "-")
            }

            OutlinedTextField(
                value = quantity.toString(),
                onValueChange = {
                    val newQuantity = it.toIntOrNull()
                    if (newQuantity != null) {
                        if (newQuantity >= 0) {
                            quantity = newQuantity
                        } else {
                            // Show Toast message for negative quantity
                            Toast.makeText(context, "Quantity cannot be negative!", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            Button(
                onClick = {
                    // Increase quantity
                    quantity++
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "+")
            }
        }


        // Calculate and display the total cost
        var totalCost = if (selectedItem == "Cup") {
            quantity * costPerCup
        } else {
            quantity * costPerCone
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth1() // Ensure the Column takes up the full width
        ) {
            Text(
                text = "Total Cost: $${String.format("%.2f", totalCost)}",
                modifier = Modifier.padding(top = 16.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center

            )
        }

    }
}

@Composable
fun ImageCenter(selectedItem: String) {
    val imageRes = R.drawable.icecream
    val image = painterResource(imageRes)
    Image(
        painter = image,
        contentDescription = null, // Provide a description if needed
        modifier = Modifier
            .padding(10.dp) // Adjust padding as needed
    )
}

@Preview
@Composable
fun AppPreview() {
    IcecreamappTheme {
        Column {
            IceCreamAppBar()
            dropDownMenu()
        }
    }
}