package storage.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBDialogType
import io.reactivex.Single

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.27
 */
@Entity
data class Dialog(@PrimaryKey
                  val dialogId: String,
                  val dialogName: String,
                  val dialogOccupants: String,
                  val dialogType: Int) {

    constructor(dialog: QBChatDialog): this(dialog.dialogId, dialog.name,
            dialog.occupants.joinToString(separator = ","), dialog.type.code)

    fun toQBChatDialog() = QBChatDialog(dialogId).apply {
        name = dialogName
        type = QBDialogType.parseByCode(dialogType)
        setOccupantsIds(dialogOccupants.split(",").map { it.toInt() })
    }
}

@Dao
interface DialogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(dialog: Dialog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(vararg dialogs: Dialog)

    @Update
    fun update(dialog: Dialog)

    @Delete
    fun remove(dialog: Dialog)

    @Query("DELETE FROM Dialog")
    fun removeAll()

    @Query("SELECT * FROM Dialog")
    fun getAllDialogs(): List<Dialog>

    @Query("SELECT * FROM Dialog")
    fun getDialogsAsync(): Single<List<Dialog>>
}