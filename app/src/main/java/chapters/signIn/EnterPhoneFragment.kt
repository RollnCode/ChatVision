package chapters.signIn

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import base.BFragment
import chapters.signIn.layers.EnterPhoneContract
import chapters.signIn.layers.EnterPhoneContract.SignInResult
import chapters.signIn.layers.EnterPhoneContract.SignInResult.ResultCodeSent
import chapters.signIn.layers.EnterPhoneContract.SignInResult.ResultComplete
import chapters.signIn.layers.EnterPhonePresenter
import chapters.signIn.layers.SignInRouter
import com.rollncode.basement.utility.extension.showAlert
import com.rollncode.basement.utility.extension.string
import com.rollncode.chatVision.R
import com.tbruyelle.rxpermissions2.RxPermissions
import instruments.checkedChangeObservable
import instruments.onClickObservable
import instruments.textChangeObservable
import kotlinx.android.synthetic.main.fragment_enter_phone.*
import storage.Settings
import view.CountrySelectorView

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class EnterPhoneFragment : BFragment(), EnterPhoneContract.View {

    companion object {
        fun instance(): EnterPhoneFragment {
            return EnterPhoneFragment()
        }
    }

    override val countryTextObservable
        get() = edtCountry.textChangeObservable()
    override val phoneTextObservable
        get() = edtPhone.textChangeObservable()
    private val presenter by lazy { EnterPhonePresenter(this) }
    private val signInRouter by lazy { activity as? SignInRouter }
    private var menuApply: MenuItem? = null

    override fun getLayoutRes()
            = R.layout.fragment_enter_phone

    override fun getMenuLayout()
            = R.menu.menu_apply

    @Suppress("ConstantConditionIf")
    override fun onViewCreated(v: View, b: Bundle?) {
        super.onViewCreated(v, b)
        RxPermissions(activity!!).request(Manifest.permission.READ_CONTACTS)
        addDisposable(edtCountryLayout.onClickObservable()
                .subscribe {
                    val view = CountrySelectorView(v.context)
                    val alert = activity?.showAlert {
                        setView(view)
                        setPositiveButton(R.string.btn_close, null)
                    }
                    view.setListener {
                        edtCountry.setText(getString(R.string.formatted_country_code, it.first, it.second))
                        alert?.dismiss()
                    }
                })
        edtCountry.movementMethod = null
        edtCountry.keyListener = null
        edtCountry.text = null
        cbIncognito.isChecked = Settings.system.isIncognito
        addDisposable(cbIncognito.checkedChangeObservable()
                .subscribe { changeTheme(it) })
        presenter.bind()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menuApply = menu?.findItem(R.id.action_ok)
        setSignInButtonVisibility(false)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_ok -> presenter.onSighInPressed(edtCountry.string, edtPhone.string)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setSignInButtonVisibility(visible: Boolean) {
        menuApply?.isVisible = visible
    }

    override fun onSignIn(result: SignInResult) {
        when (result) {
            is ResultComplete -> signInRouter?.onGetCredential(result.credential)
            is ResultCodeSent -> signInRouter?.onCodeSent(result.verificationId, result.token)
        }
    }

    @SuppressLint("NewApi")
    private fun changeTheme(dark: Boolean) {
        activity?.setTheme(if (dark) R.style.AppTheme_Dark else R.style.AppTheme_Light)
        Settings.system.isIncognito = dark

        val textColor = getColor(R.attr.aTextColor)
        val backgroundColor = getColor(R.attr.aBackGroundColor)

        if (dark || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            changeColors(backgroundColor, textColor)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val x = layoutRoot.width / 2
            val y = layoutRoot.height / 2
            val finalRadius = Math.hypot(x.toDouble(), y.toDouble()).toFloat()
            val startRadius = 0F
            val anim = if (dark) ViewAnimationUtils.createCircularReveal(layoutRoot, x, y, startRadius, finalRadius)
            else ViewAnimationUtils.createCircularReveal(layoutRoot, x, y, finalRadius, startRadius)
            anim.duration = 300
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    changeColors(backgroundColor, textColor)
                }
            })
            anim.start()
        }
    }

    private fun getColor(attr: Int): Int {
        val typedValue = TypedValue()
        activity?.theme?.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun changeColors(backgroundColor: Int, textColor: Int) {
        layoutRoot.setBackgroundColor(backgroundColor)
        edtPhone.setTextColor(textColor)
        edtCountry.setTextColor(textColor)
        cbIncognito.setTextColor(textColor)
    }
}