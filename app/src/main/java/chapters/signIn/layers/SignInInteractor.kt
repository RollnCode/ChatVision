package chapters.signIn.layers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.quickblox.users.model.QBUser
import instruments.DB_PASSWORDS_KEY
import instruments.DB_URL
import instruments.QBHelper
import io.reactivex.Single
import utils.WrongPhoneException
import java.util.*

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.31
 */
class SignInInteractor : SignInContract.Interactor {
    override fun signIn(credential: PhoneAuthCredential): Single<Task<AuthResult>> {
        return Single.create { emitter ->
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener {
                    emitter.onSuccess(it)
                }
        }
    }

    override fun checkUser(phone: String?): Single<QBUser> {
        return Single.create<String> { emitter ->
            if (phone == null) {
                emitter.onError(WrongPhoneException())
                return@create
            }
            val reference = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL).child(DB_PASSWORDS_KEY)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError?) {
                    reference.removeEventListener(this)
                    emitter.onError(Exception(error?.message))
                }

                override fun onDataChange(data: DataSnapshot?) {
                    reference.removeEventListener(this)
                    data ?: return
                    if (data.hasChild(phone)) {
                        emitter.onSuccess(data.child(phone).value.toString())
                    } else {
                        val pass = UUID.randomUUID().toString()
                        reference.child(phone).setValue(pass).addOnCompleteListener {
                            emitter.onSuccess(pass)
                        }
                    }
                }

            })
        }
            .flatMap { QBHelper.signIn(QBHelper.getQBLogin(phone), it) }
    }
}