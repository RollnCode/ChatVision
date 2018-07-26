package chapters.chatRoom.layers

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import base.BPresenter
import chapters.chatRoom.layers.CreateDialogContracts.View

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2018.05.03
 */
class CreateDialogPresenter(private val view: View) : BPresenter(view), LifecycleObserver {

    init {
        view.lifecycle.addObserver(this)
    }

    private val interactor: CreateDialogContracts.Interactor = CreateDialogIneractor()

    @Suppress("ProtectedInFinal", "MoveLambdaOutsideParentheses")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun bind() {
        view.addDisposable(view.createObservable
            .subscribe {
                execute(interactor.createDialog(it.ids, it.title, it.isPublic, it.img), {
                    view.onDialogCreated()
                })
            })
    }
}