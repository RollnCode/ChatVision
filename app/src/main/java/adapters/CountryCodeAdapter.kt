package adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModelWithView
import com.rollncode.chatVision.R

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class CountryCodeAdapter : EpoxyAdapter() {

    private var listener: (Pair<String, String>) -> Unit = {}

    fun setData(codes: Map<String, String>) {
        removeAllModels()
        addModels(codes.map { CountryCodeModel(it.toPair()) })
    }

    fun setListener(l: (Pair<String, String>) -> Unit) {
        listener = l
    }

    fun updateList(str: String) {
        models.forEach {
            it as CountryCodeModel
            if (it.isContains(str) && !it.isShown) {
                it.show()
                notifyModelChanged(it)
            } else if (!it.isContains(str) && it.isShown) {
                it.hide()
                notifyModelChanged(it)
            }
        }
    }

    inner class CountryCodeModel(private val item: Pair<String, String>) : EpoxyModelWithView<TextView>() {

        @SuppressLint("InflateParams")
        override fun buildView(parent: ViewGroup): TextView
                = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item, null) as TextView

        override fun bind(view: TextView) {
            view.setBackgroundResource(R.drawable.selector_transparent)
            view.text = view.context.getString(R.string.formatted_country_code, item.first, item.second)
            view.setOnClickListener { listener(item) }
        }

        fun isContains(str: String)
                = str.isBlank() || item.first.contains(str, true) || item.second.contains(str, true)
    }
}