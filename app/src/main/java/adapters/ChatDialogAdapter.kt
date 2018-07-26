package adapters

import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelWithView
import com.airbnb.epoxy.EpoxyViewHolder
import com.quickblox.chat.model.QBChatMessage
import com.quickblox.users.model.QBUser
import instruments.isIncoming
import io.reactivex.Observable
import kotlinx.android.synthetic.main.view_dialog_item.view.*
import view.DialogItemView

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.11.17
 */
class ChatDialogAdapter(private val userClickListener: (QBUser) -> Unit) : EpoxyAdapter() {

    private val users = mutableListOf<QBUser>()
    private var listener: (String) -> Unit = {}
    val scrollObservable: Observable<String> = Observable.create { emitter ->
        listener = { emitter.onNext(it) }
    }

    override fun onModelBound(holder: EpoxyViewHolder?, model: EpoxyModel<*>?, position: Int) {
        super.onModelBound(holder, model, position)
        if (position == models.lastIndex && !models.isEmpty()) {
            (models.last() as? MessageModel)?.let { listener(it.message.id) }
        }
    }

    fun setUsers(list: List<QBUser>) {
        users.clear()
        users.addAll(list)
    }

    fun setData(list: List<QBChatMessage>) {
        removeAllModels()
        addModels(list.map { MessageModel(it) })
    }

    fun addMessages(list: List<QBChatMessage>) {
        val lastModel = models.last()
        addModels(list.map { MessageModel(it) })
        notifyModelChanged(lastModel)
    }

    fun newMessage(msg: QBChatMessage) {
        if (models.isEmpty()) addModel(MessageModel(msg))
        else insertModelBefore(MessageModel(msg), models[0])
    }

    fun setMessageRead(id: String) {
        models.find { (it as? MessageModel)?.message?.id == id }?.let {
            (it as MessageModel).message.readIds.add(-1)
            notifyModelChanged(it)
        }
    }

    fun updateSentMessage(message: QBChatMessage) {
        if (models.isEmpty()) addModel(MessageModel(message))
        else models.forEach { model ->
            if ((model as? MessageModel)?.message?.dateSent ?: 0 < message.dateSent) {
                insertModelBefore(MessageModel(message), model)
                return
            }
        }
    }

    fun getMessageList() =
            models.mapNotNull { (it as? MessageModel)?.message }

    inner class MessageModel(val message: QBChatMessage) : EpoxyModelWithView<DialogItemView>() {

        override fun buildView(parent: ViewGroup): DialogItemView =
                DialogItemView(parent.context)

        override fun bind(view: DialogItemView) {
            val user = users.find { it.id == message.senderId } ?: QBUser()
            view.setMessage(message, user, isSameSenderAsPrevious())
            if (message.isIncoming) {
                view.imgCompanionAvatar.setOnClickListener { userClickListener(user) }
            }
        }

        private fun isSameSenderAsPrevious(): Boolean {
            val position = getModelPosition(this)
            return (position != models.lastIndex && (models[position + 1] as? MessageModel)?.message?.senderId == message.senderId)
        }
    }
}