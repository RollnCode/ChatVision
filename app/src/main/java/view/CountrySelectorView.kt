package view

import adapters.CountryCodeAdapter
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.rollncode.basement.utility.extension.getScreenSize
import com.rollncode.chatVision.R
import instruments.textChangeObservable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_country_selector.view.*
import java.io.LineNumberReader

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.25
 */
class CountrySelectorView(context: Context, attributeSet: AttributeSet? = null) : ConstraintLayout(context, attributeSet) {

    private val adapter by lazy { CountryCodeAdapter() }
    private val compositeDisp = CompositeDisposable()

    init {
        inflate(context, R.layout.view_country_selector, this)
        val input = LineNumberReader(context.assets.open("country_codes.txt").reader())
        val countryMap = input.readLines().filter { it.isNotBlank() }.sorted()
            .associate { Pair(it.substringBefore("/").trim(), it.substringAfter("/").trim()) }
        input.close()
        recyclerView.adapter = adapter
        adapter.setData(countryMap)
        compositeDisp.add(edtCountryCode.textChangeObservable().subscribe { adapter.updateList(it) })
        val pad = context.resources.getDimensionPixelOffset(R.dimen.general_mid)
        setPaddingRelative(pad, pad, pad, pad)
        minWidth = context.getScreenSize().x / 3 * 2
    }

    override fun setLayoutParams(params: android.view.ViewGroup.LayoutParams?) {
        params?.width = LayoutParams.MATCH_PARENT
        params?.height = LayoutParams.WRAP_CONTENT
        super.setLayoutParams(params)
    }

    fun setListener(listener: (Pair<String, String>) -> Unit) {
        adapter.setListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisp.clear()
    }
}