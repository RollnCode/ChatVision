package com.rollncode.basement.interfaces

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.29
 */
interface IDataTenant<in DATA> {

    fun onDataBind(value: DATA, vararg params: Any)
}