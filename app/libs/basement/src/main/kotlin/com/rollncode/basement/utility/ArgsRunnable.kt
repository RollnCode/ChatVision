package com.rollncode.basement.utility

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.25
 */
abstract class ArgsRunnable : Runnable {

    private var values: Array<Any> = arrayOf()

    fun setArgs(vararg values: Any): ArgsRunnable {
        @Suppress("UNCHECKED_CAST")
        this.values = values as Array<Any>
        return this
    }

    override fun run() {
        try {
            run(*values)

        } finally {
            values = arrayOf()
        }
    }

    abstract fun run(vararg values: Any)
}