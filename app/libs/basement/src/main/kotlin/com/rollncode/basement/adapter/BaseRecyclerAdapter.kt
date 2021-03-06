package com.rollncode.basement.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rollncode.basement.interfaces.IDataTenant

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.29
 */
abstract class BaseRecyclerAdapter<in DATA>(vararg values: DATA)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val values = values.toMutableList()

    final override fun getItemCount(): Int
            = values.size

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(onCreateView(parent.context, viewType))
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(holder.itemView, position, values[position], holder.itemViewType)
    }

    protected abstract fun onCreateView(context: Context, type: Int): View

    @Suppress("UNCHECKED_CAST")
    protected open fun onBindView(view: View, position: Int, value: DATA, type: Int) {
        if (view is IDataTenant<*>) {
            (view as IDataTenant<DATA>).onDataBind(value, position, type)
        }
    }

    fun setData(vararg values: DATA) {
        this.values.clear()
        addData(*values)
    }

    fun addData(vararg values: DATA) {
        this.values += values
    }
}

private class ViewHolder(view: View) : RecyclerView.ViewHolder(view)