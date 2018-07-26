package chapters.chatRoom

import adapters.SimpleUsersAdapter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import base.BFragment
import chapters.chatRoom.layers.CreateDialogContracts
import chapters.chatRoom.layers.CreateDialogContracts.CreateDialogData
import chapters.chatRoom.layers.CreateDialogPresenter
import com.quickblox.users.model.QBUser
import com.rollncode.basement.utility.extension.string
import com.rollncode.chatVision.R
import instruments.textChangeObservable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_create_chat.*
import storage.Settings
import utils.OfflineException

@Suppress("UNCHECKED_CAST")
/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.03
 */
class CreateChatFragment : BFragment(), CreateDialogContracts.View {

    companion object {
        private const val USERS_KEY = "USERS_KEY"
        fun instance(users: ArrayList<QBUser>): CreateChatFragment = CreateChatFragment().apply {
            arguments = Bundle().apply { putSerializable(USERS_KEY, users) }
            CreateDialogPresenter(this)
        }
    }

    override val createObservable: Observable<CreateDialogData> = Observable.create { emitter ->
        listener = { emitter.onNext(it) }
    }
    private val users by lazy { (arguments?.getSerializable(USERS_KEY) as? List<QBUser>) ?: listOf() }
    private var listener: (CreateDialogData) -> Unit = {}
    private var avatar = ""

    override fun getLayoutRes() = R.layout.fragment_create_chat

    override fun getMenuLayout() = R.menu.menu_apply

    override fun onViewCreated(v: View, b: Bundle?) {
        super.onViewCreated(v, b)
        addDisposable(edtTitle.textChangeObservable()
            .subscribe { if (it.isNotBlank()) layoutTitle.error = null })
        activity?.setTitle(R.string.create_dialog)
        recyclerView.adapter = SimpleUsersAdapter(users)
    }

    @Suppress("UsePropertyAccessSyntax")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_ok) {
            if (Settings.system.isOnline) checkData()?.let(listener) ?: layoutTitle.setError(getString(R.string.title_required))
            else onRequestError(OfflineException())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkData(): CreateDialogData? {
        if (edtTitle.string.isBlank()) return null
        return CreateDialogData(users.map { it.id }, edtTitle.string, swPublic.isChecked, avatar)
    }

    override fun onDialogCreated() {

    }
}