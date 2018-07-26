package storage.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.quickblox.chat.model.QBChatMessage
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.30
 */
@Entity
data class Message(@PrimaryKey
                   val msgId: String,
                   val msgSenderId: Int,
                   val msgBody: String,
                   val msgDate: Long,
                   @ColumnInfo(name = "dialog_id")
                   val msgDialogId: String) {

    constructor(msg: QBChatMessage) : this(msg.id, msg.senderId, msg.body, msg.dateSent, msg.dialogId)

    fun toChatMessage() = QBChatMessage().apply {
        id = msgId
        senderId = msgSenderId
        body = msgBody
        dateSent = msgDate
        dialogId = msgDialogId
    }
}

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(vararg messages: Message)

    @Update
    fun update(message: Message)

    @Delete
    fun remove(message: Message)

    @Query("DELETE FROM Message")
    fun removeAll()

    @Query("SELECT * FROM Message")
    fun getAllMessages(): List<Message>

    @Query("SELECT * FROM Message")
    fun getMessagesAsync(): Single<List<Message>>

    @Query("SELECT * FROM Message WHERE dialog_id=:dialogId")
    fun getDialogMessagesAsync(dialogId: String): Single<List<Message>>
}