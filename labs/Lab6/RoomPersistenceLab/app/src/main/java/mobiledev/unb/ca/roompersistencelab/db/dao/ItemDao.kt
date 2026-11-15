package mobiledev.unb.ca.roompersistencelab.db.dao

import androidx.room3.*
import mobiledev.unb.ca.roompersistencelab.db.entities.Item

/**
 * This DAO object validates the SQL at compile-time and associates it with a method
 */
@Dao
interface ItemDao {
    // TODO
    //  Add query to insert new record
    //  Additional details can be found at https://developer.android.com/reference/kotlin/androidx/room3/Insert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertItem(item: Item)

    // TODO
    //   Add query to search for items by name sorted by number in descending order
    //   HINT: This should return a list or Items (do not use Flow here)
    //   Additional details can be found at https://developer.android.com/reference/kotlin/androidx/room3/Query
    @Query("SELECT * FROM items WHERE name = :name ORDER BY num DESC")
    fun getAllItems(name: String): List<Item>
}