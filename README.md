# Ice Cream Order App

## Overview

The Ice Cream Order App is an Android application developed in Kotlin designed to offer users a customizable ice cream ordering experience. Users can choose between a cup or cone, specify the quantity, and see the total cost calculated automatically.

## Features

- **Customizable Orders**: Choose your ice cream type and quantity.
- **Dynamic Pricing**: Automatically calculates the total cost based on selections.
- **User-Friendly UI**: Intuitive design for an enhanced ordering experience using Material UI.

## Main Components

- **MainActivity**: The entry point of the app.
- **IceCreamAppBar**: Displays the top app bar with the title.
- **dropDownMenu**: Manages ice cream type and quantity selections.
- **ImageCenter**: Shows images corresponding to the selected ice cream type.

## Functionality

- **Holder Type Selection**: Choose between a cup or cone.
- **Quantity Adjustment**: Increase or decrease your order quantity.
- **Total Cost Calculation**: View the cost updated in real-time.
- **Error Handling**: Prevents ordering negative quantities.

## Extension Features [Branch](https://github.com/vishwa-3g/icecreamapp/tree/LittleIceCream_extended)

- **Coupon Code Application**: Enter a code for discounts.
- **Dynamic Image Updates**: Visual feedback on holder selection.
- **Toast Message**: Displays a popup confirming the coupon code application and updated total cost.
- **Enhanced Type Selection**: Improved UI with dropdown menus for ice cream type selection.

## Database Integration [Branch](https://github.com/vishwa-3g/icecreamapp/tree/IcecreamRoomDb)

The Ice Cream Order App incorporates a Room database for secure order storage. Each order is assigned a unique auto-generated ID as the primary key.

### Order Management
- **Show All Orders**: Displays all orders stored in the database.
- **Show Top 10 Orders**: Lists the top 10 orders sorted by total price in descending order.
- **Show Orders Over $50**: Filters and displays only orders where the total cost exceeds $50.
- **Order Deletion**: Users can delete individual orders from the order history. Once deleted, the order is permanently removed from the database.

### Order Placement
- When an order is placed, details such as selected flavours, quantities, and total cost are inserted into the database using Room Persistence Library.
- The order is confirmed via a toast message.

### Order History
- Users can navigate to the Order History screen to view all past orders.
- The database supports filtering options for quick access to specific order categories.


## Getting Started

Clone the repository and follow the setup instructions to run the app on your Android device or emulator.

```bash
git clone https://github.com/vishwa-3g/icecreamapp.git
```

