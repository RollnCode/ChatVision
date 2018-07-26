package instruments

import android.support.v4.widget.SwipeRefreshLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import application.ChatApp
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import com.rollncode.chatVision.BuildConfig
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import storage.room.DialogDao
import storage.room.MessageDao
import storage.room.UserDao

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
val DEBUG = BuildConfig.DEBUG

@Suppress("ConstantConditionIf", "unused")
inline fun <reified T> T.toLog(msg: String) {
    if (DEBUG) Log.d("-=-", "${T::class.simpleName}: $msg")
}

//fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

//fun Context.toast(@StringRes msg: Int) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

//fun Activity.hideKeyBoard() {
//    val view = this.currentFocus
//    view ?: return
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    imm.hideSoftInputFromWindow(view.windowToken, 0)
//}

fun EditText.textChangeObservable(): Observable<String> {
    return Observable.create { emitter ->
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emitter.onNext(s.toString())
            }
        }
        this.addTextChangedListener(listener)
        emitter.setDisposable(Disposables.fromAction { this.removeTextChangedListener(listener) })
    }
}

//fun View.focusChangeObservable(): Observable<Boolean> {
//    return Observable.create { emitter ->
//        setOnFocusChangeListener { _, focus -> emitter.onNext(focus) }
//    }
//}

fun View.onClickObservable(): Observable<Unit> {
    return Observable.create { emitter ->
        setOnClickListener { emitter.onNext(Unit) }
    }
}

fun CompoundButton.checkedChangeObservable(): Observable<Boolean> {
    return Observable.create { emitter ->
        setOnCheckedChangeListener { _, checked -> emitter.onNext(checked) }
    }
}

fun SwipeRefreshLayout.onRefreshObservable(): Observable<Unit> {
    return Observable.create { emitter ->
        setOnRefreshListener { emitter.onNext(Unit) }
    }
}

//inline fun View.waitForLayout(crossinline f: () -> Unit) = with(viewTreeObserver) {
//    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//        override fun onGlobalLayout() {
//            if (this@with.isAlive) removeOnGlobalLayoutListener(this)
//            f()
//        }
//    })
//}

val QBUser.userName: String
    get() = fullName ?: shortLogin

val QBUser.shortLogin: String
    get() = login?.substringBefore("@") ?: "DELETED"

val QBChatMessage.isIncoming: Boolean
    get() = QBHelper.isIncomingMessage(this)

val userDb: UserDao
    get() = ChatApp.dataBase.getUserDao()

val dialogDb: DialogDao
    get() = ChatApp.dataBase.getDialogDao()

val messageDb: MessageDao
    get() = ChatApp.dataBase.getMessageDao()