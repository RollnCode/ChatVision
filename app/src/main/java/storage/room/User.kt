package storage.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.quickblox.users.model.QBUser
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.24
 */
@Entity
data class User(@PrimaryKey
                val userId: Int,
                val userLogin: String,
                val name: String?) {

    constructor(user: QBUser): this(user.id, user.login, user.fullName)

    fun toQBUser() = QBUser(userId).apply {
        login = userLogin
        fullName = name
    }
}

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(vararg users: User)

    @Update
    fun update(user: User)

    @Delete
    fun remove(user: User)

    @Query("DELETE FROM User")
    fun removeAll()

    @Query("SELECT * FROM User")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM User")
    fun getUsersAsync(): Single<List<User>>

    @Query("SELECT * FROM User WHERE userId IN (:ids)")
    fun getUsersByIds(ids: List<Int>): Single<List<User>>
}