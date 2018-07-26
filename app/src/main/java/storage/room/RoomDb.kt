package storage.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.29
 */
@Database(entities = [User::class, Dialog::class, Message::class], version = 1, exportSchema = false)
abstract class RoomDb : RoomDatabase() {
    abstract fun getUserDao(): UserDao
    abstract fun getDialogDao(): DialogDao
    abstract fun getMessageDao(): MessageDao

    fun clear() {
        getUserDao().removeAll()
        getDialogDao().removeAll()
        getMessageDao().removeAll()
    }
}