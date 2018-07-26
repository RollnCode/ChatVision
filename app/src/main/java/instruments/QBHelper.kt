package instruments

import android.content.Context
import android.os.Bundle
import com.quickblox.auth.session.QBSession
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.auth.session.QBSessionManager.QBSessionListener
import com.quickblox.auth.session.QBSessionParameters
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.exception.QBChatException
import com.quickblox.chat.listeners.QBChatDialogMessageListener
import com.quickblox.chat.listeners.QBMessageStatusListener
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.chat.model.QBDialogType
import com.quickblox.chat.model.QBDialogType.GROUP
import com.quickblox.chat.request.QBDialogRequestBuilder
import com.quickblox.chat.request.QBMessageGetBuilder
import com.quickblox.chat.utils.DialogUtils
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.request.QBPagedRequestBuilder
import com.quickblox.core.request.QBRequestGetBuilder
import com.quickblox.core.server.Performer
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBAddressBookContact
import com.quickblox.users.model.QBUser
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.disposables.Disposables
import io.reactivex.functions.Action
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import storage.Settings
import utils.ObservableMessage
import utils.ObservableMessageStatus
import utils.UnauthorizedException
import utils.UnknownException
import java.lang.Exception

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.02
 */
object QBHelper {

    val isUserSignIn: Boolean
        get() = sessionParams != null
    val isUserLoggedIn: Boolean
        get() = chatService.isLoggedIn
    private val sessionListener by lazy { initSessionListener() }
    private val connectionListener by lazy { initConnectionListener() }
    private val chatService by lazy { QBChatService.getInstance() }
    private val sessionParams
        get() = QBSessionManager.getInstance().sessionParameters

    fun init(context: Context) {
        if (!Settings.system.isOnline) return
        // ------------------------------- QBSettings ---------------------------------
        //        QBSettings.getInstance().storingMehanism = StoringMechanism.UNSECURED
        QBSettings.getInstance().init(context, QB_APP_ID, QB_AUTH_KEY, QB_AUTH_SECRET)
        QBSettings.getInstance().accountKey = QB_ACCOUNT_KEY
        //        QBSettings.getInstance().setEndpoints("???", QB_CHAT_ENDPOINT, PRODUCTION)
        //        QBHttpConnectionConfig.setConnectTimeout(30000)
        QBSessionManager.getInstance().addListener(sessionListener)

        // ------------------------------- QBChatService ---------------------------------
        QBChatService.setDebugEnabled(true) // enable chat logging
        val chatServiceConfigurationBuilder = QBChatService.ConfigurationBuilder()
        chatServiceConfigurationBuilder.socketTimeout = 60 //Sets chat socket's read timeout in seconds //todo what this for?!!!
        chatServiceConfigurationBuilder.isKeepAlive = true //Sets connection socket's keepAlive option.
        chatServiceConfigurationBuilder.isUseTls = true //Sets the TLS security mode used when making the connection. By default TLS is disabled.
        chatServiceConfigurationBuilder.isAutojoinEnabled = true
        chatServiceConfigurationBuilder.setAutoMarkDelivered(false)
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder)
        //        QBChatService.setDefaultPacketReplyTimeout(10000) //set reply timeout in milliseconds for connection's packet.
        chatService.addConnectionListener(connectionListener)
        chatService.setUseStreamManagement(true)
    }

    fun getQBLogin(phone: String?) =
            phone?.let { "${it.filter(Char::isDigit)}$USER_LOGIN_SUFFIX" } ?: ""

    fun signIn(login: String, password: String): Single<QBUser> {
        return Single.create { emitter ->
            QBUsers.signIn(login, password).performAsync(getSingleCallback(emitter,
                    err = {
                        if (it?.httpStatusCode == 401) { // code when user is not registered
                            signUp(login, password, emitter)
                        } else {
                            emitter.onError(it)
                        }
                    }))
        }
    }

    private fun signIn(login: String, password: String, emitter: SingleEmitter<QBUser>) =
            QBUsers.signIn(login, password).performAsync(getSingleCallback(emitter))

    @Suppress("MoveLambdaOutsideParentheses")
    private fun signUp(login: String, password: String, emitter: SingleEmitter<QBUser>) =
            QBUsers.signUp(QBUser(login, password)).performAsync(getSingleCallback(emitter, { _, _ ->
                signIn(login, password, emitter)
            }))

    fun signOut(): Completable {
        return Completable.create { emitter ->
            QBUsers.signOut().performAsync(getCompletableCallback(emitter))
        }
    }

    // user have to log in for sending messages
    // for logging in user's password needed
    fun logIn(user: QBUser): Completable {
        return Completable.create { emitter ->
            chatService.login(user, getCompletableCallback<Any?>(emitter,
                    err = {
                        if (it?.httpStatusCode == -1) emitter.onComplete()
                        else emitter.onError(it)
                    }))
        }
    }

    fun logOut(): Completable {
        return Completable.create { emitter ->
            chatService.logout(getCompletableCallback(emitter))
            //            chatService.destroy()
        }
    }

    fun getCurrentUser(): Single<QBUser> {
        return sessionParams?.let { params ->
            getUserById(params.userId).map {
                it.apply { password = params.userPassword }
            }
        } ?: Single.error(UnauthorizedException())
    }

    fun updateUser(user: QBUser): Single<QBUser> {
        if (user.oldPassword == null) user.oldPassword = user.password
        return Single.create { emitter ->
            QBUsers.updateUser(user).performAsync(getSingleCallback(emitter))
        }
    }

    private fun getUsers(page: Int, perPage: Int = USERS_PER_PAGE): Performer<ArrayList<QBUser>> {
        return QBUsers.getUsers(QBPagedRequestBuilder()
                .setPage(page)
                .setPerPage(perPage))
    }

    fun getAllUsers(): Single<List<QBUser>> {
        val list = mutableListOf<QBUser>()
        return Single.create { emitter ->
            var i = 0
            fun circleRequest() {
                getUsers(++i).performAsync(getCallback({ users, _ ->
                    if (users.isNotEmpty()) {
                        list.addAll(users)
                        circleRequest()
                    } else {
                        emitter.onSuccess(list)
                    }
                }, { emitter.onError(it ?: UnknownException()) }))
            }
            circleRequest()
        }
    }

    @Suppress("unused")
    fun getUsersByPages(page: Int): Flowable<List<QBUser>> {
        return Flowable.create({ emitter ->
            getUsers(page).performAsync(getCallback({ users, _ ->
                if (users.isNotEmpty()) {
                    emitter.onNext(users)
                } else {
                    emitter.onComplete()
                }
            }, { emitter.onError(it ?: UnknownException()) }))
        }, BUFFER)
    }

    @Suppress("unused")
    fun getUserByLogin(login: String): Single<QBUser> {
        return Single.create { emitter ->
            QBUsers.getUserByLogin(login).performAsync(getSingleCallback(emitter))
        }
    }

    private fun getUserById(id: Int): Single<QBUser> {
        return Single.create { emitter ->
            QBUsers.getUser(id).performAsync(getSingleCallback(emitter))
        }
    }

    fun getUsersByIds(ids: List<Int>): Single<ArrayList<QBUser>> {
        return Single.create { emitter ->
            val pagedRequestBuilder = QBPagedRequestBuilder()
                    .setPerPage(ids.size)
            QBUsers.getUsersByIDs(ids, pagedRequestBuilder).performAsync(getSingleCallback(emitter))
        }
    }

    @Suppress("unused")
    fun deleteUser(): Completable {
        val id = sessionParams?.userId ?: 0
        return Completable.create { emitter ->
            QBUsers.deleteUser(id).performAsync(getCompletableCallback(emitter))
        }
    }

    @Suppress("unused")
    fun getAddressBook(): Single<ArrayList<QBAddressBookContact>> {
        return Single.create { emitter ->
            QBUsers.getAddressBook(null).performAsync(getSingleCallback(emitter))
        }
    }

    // -------------------------------------- DIALOGS ----------------------------------------------

    fun createDialog(ids: List<Int>, name: String, type: QBDialogType = GROUP, photo: String = ""): Single<QBChatDialog> {
        return Single.create { emitter ->
            val dialog = DialogUtils.buildDialog(name, type, ids).apply { setPhoto(photo) }
            QBRestChatService.createChatDialog(dialog).performAsync(getSingleCallback(emitter))
        }
    }

    fun getDialogs(): Single<ArrayList<QBChatDialog>> {
        return Single.create { emitter ->
            QBRestChatService.getChatDialogs(null, QBRequestGetBuilder())
                    .performAsync(getSingleCallback(emitter))
        }
    }

    fun getDialog(dialogId: String): Single<QBChatDialog> {
        return Single.create { emitter ->
            QBRestChatService.getChatDialogById(dialogId)
                    .performAsync(getSingleCallback(emitter))
        }
    }

    @Suppress("unused")
    fun addUserToDialog(dialog: QBChatDialog, ids: List<Int>, name: String = dialog.name) {
        dialog.name = name
        QBRestChatService.updateGroupChatDialog(dialog, QBDialogRequestBuilder().apply { addUsers(*ids.toIntArray()) })
    }

    fun deleteDialog(dialogId: String): Completable {
        return Completable.create { emitter ->
            QBRestChatService.deleteDialog(dialogId, true).performAsync(getCompletableCallback(emitter))
        }
    }

    // -------------------------------------- MESSAGES ---------------------------------------------

    fun createMessage(msg: String) = QBChatMessage().apply {
        body = msg
        senderId = sessionParams.userId
        isMarkable = true
    }

    fun sendMessage(dialog: QBChatDialog, message: QBChatMessage, saveToHistory: Boolean = true): Completable {
        if (!isUserLoggedIn) return Completable.error(UnauthorizedException())
        dialog.initForChat(chatService)
        message.setSaveToHistory(saveToHistory)
        return Completable.create { emitter ->
            try {
                dialog.sendMessage(message, getCompletableCallback(emitter))
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }
    }

    fun getChatMessages(dialog: QBChatDialog, dialogLimit: Int = DIALOG_MESSAGES_LIMIT, sortDesc: Boolean = true): Single<ArrayList<QBChatMessage>> {
        return Single.create { emitter ->
            QBRestChatService.getDialogMessages(dialog, QBMessageGetBuilder().apply {
                limit = dialogLimit
                markAsRead(false)
//                markAsRead(!Settings.system.isIncognito)
                if (sortDesc) sortDesc(SORT_BY_DATE) else sortAsc(SORT_BY_DATE)
            })
                    .performAsync(getSingleCallback(emitter))
        }
    }

    fun observeIncomingMessages(dialog: QBChatDialog? = null): Flowable<ObservableMessage> {
        toLog("observeIncomingMessages dialog = $dialog")
        return Flowable.create({ emitter ->
            val listener = object : QBChatDialogMessageListener {
                override fun processMessage(dialogId: String, message: QBChatMessage, senderId: Int) {
                    emitter.onNext(Triple(dialogId, message, senderId))
                }

                override fun processError(dialogId: String, error: QBChatException?, message: QBChatMessage, senderId: Int) {
                    emitter.onError(error ?: UnknownException())
                }
            }
            val action: Action =
                    if (dialog == null) {
                        chatService.incomingMessagesManager.addDialogMessageListener(listener)
                        Action { chatService.incomingMessagesManager.removeDialogMessageListrener(listener) }
                    } else {
                        dialog.addMessageListener(listener)
                        Action { dialog.removeMessageListrener(listener) }
                    }
            emitter.setDisposable(Disposables.fromAction(action))
        }, BUFFER)
    }

    fun observeMessagesStatus(): Flowable<ObservableMessageStatus> {
        return Flowable.create({ emitter ->
            val listener = object : QBMessageStatusListener {
                override fun processMessageDelivered(messageId: String, dialogId: String, userId: Int) {
                    emitter.onNext(Triple(Pair(STATE_DELIVERED, messageId), dialogId, userId))
                }

                override fun processMessageRead(messageId: String, dialogId: String, userId: Int) {
                    emitter.onNext(Triple(Pair(STATE_READ, messageId), dialogId, userId))
                }
            }
            chatService.messageStatusesManager.addMessageStatusListener(listener)
            emitter.setDisposable(Disposables.fromAction { chatService.messageStatusesManager.removeMessageStatusListener(listener) })
        }, BUFFER)
    }

    @Suppress("unused")
    fun deleteMessages(ids: Set<String>): Completable {
        return Completable.create { emitter ->
            QBRestChatService.deleteMessages(ids, true).performAsync(getCompletableCallback(emitter))
        }
    }

    fun isIncomingMessage(msg: QBChatMessage) =
            msg.senderId != sessionParams?.userId ?: Settings.system.currentUserId

    private fun <T> getSingleCallback(emitter: SingleEmitter<T>,
                                      success: (T, Bundle?) -> Unit = { t, _ -> emitter.onSuccess(t) },
                                      err: (QBResponseException?) -> Unit = { emitter.onError(it) }) =
            getCallback(success, err)

    private fun <T> getCompletableCallback(emitter: CompletableEmitter,
                                           success: (T, Bundle?) -> Unit = { _, _ -> emitter.onComplete() },
                                           err: (QBResponseException?) -> Unit = { emitter.onError(it) }) =
            getCallback(success, err)

    private fun <T> getCallback(success: (T, Bundle?) -> Unit, err: (QBResponseException?) -> Unit): QBEntityCallback<T> {
        return object : QBEntityCallback<T> {
            override fun onError(error: QBResponseException?) {
                err(error)
            }

            override fun onSuccess(t: T, args: Bundle?) {
                success(t, args)
            }
        }
    }

    private fun initSessionListener(): QBSessionListener
            = object : QBSessionListener {
        override fun onSessionUpdated(parameters: QBSessionParameters?) {
//            toLog("onSessionUpdated ${parameters?.userLogin}, ${parameters?.userPassword}, ${parameters?.accessToken}")
        }

        override fun onSessionCreated(session: QBSession?) {
//            toLog("onSessionCreated ${session?.token}, ${session?.id}")
        }

        override fun onSessionExpired() {
//            toLog("onSessionExpired ")
        }

        override fun onSessionDeleted() {
//            toLog("onSessionDeleted ")
        }

        override fun onSessionRestored(session: QBSession?) {
//            toLog("onSessionRestored  ${session?.token}")
        }

        override fun onProviderSessionExpired(provider: String?) {
//            toLog("onProviderSessionExpired $provider")
        }
    }

    private fun initConnectionListener(): ConnectionListener
            = object : ConnectionListener {
        override fun connected(connection: XMPPConnection?) {
//            toLog("connected")
        }

        override fun connectionClosed() {
//            toLog("connectionClosed")
        }

        override fun connectionClosedOnError(error: Exception?) {
//            toLog("connectionClosedOnError")
        }

        override fun reconnectionSuccessful() {
//            toLog("reconnectionSuccessful")
        }

        override fun authenticated(connection: XMPPConnection?, b: Boolean) {
//            toLog("authenticated")
        }

        override fun reconnectionFailed(error: Exception?) {
//            toLog("reconnectionFailed")
        }

        override fun reconnectingIn(seconds: Int) {
//            toLog("reconnectingIn, seconds = $seconds")
        }
    }
}

const val STATE_DELIVERED = "delivered"
const val STATE_READ = "read"