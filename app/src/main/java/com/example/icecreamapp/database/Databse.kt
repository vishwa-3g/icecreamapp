package com.example.icecreamapp.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.icecreamapp.db

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

// Insert the new order into the database
fun placeOrder(items: Int, totalCost: Double) {
    val newOrder = Order(items = items, totalCost = totalCost)
    db.orderDao().insertAll(newOrder)
}