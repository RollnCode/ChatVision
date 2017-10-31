package com.rollncode.basement.application

import android.app.*
import android.app.Application.*
import android.content.*
import android.net.*
import android.os.*
import android.support.annotation.*
import com.rollncode.basement.utility.*
import java.lang.ref.*

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 * @since 2017.09.20
 */
abstract class BaseApp : Application(), ActivityLifecycleCallbacks {

    val mainHandler by lazy { Handler() }
    private val workHandler by lazy { WorkHandler(this) }
    protected val activityResumePause by lazy { mutableSetOf<String>() }
    protected val activityCreatedDestroyed by lazy { mutableSetOf<String>() }
    protected val connectivityManager by lazy { getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    override fun onCreate() {
        super.onCreate()

        super.registerActivityLifecycleCallbacks(this)
        super.registerReceiver(internetStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    @WorkerThread
    abstract fun onInit();

    @WorkerThread
    abstract fun onExit();

    abstract fun getInternetStateCode(): Int

    fun post(run: Runnable, delay: Long = 0, mainLooper: Boolean = true, removePrevious: Boolean = true) {
        val handler = if (mainLooper) mainHandler else workHandler

        if (removePrevious) {
            handler.removeCallbacks(run)
        }
        handler.postDelayed(run, delay)
    }

    @CallSuper
    override fun onActivityCreated(a: Activity?, b: Bundle?) {
        if (activityCreatedDestroyed.size == 0) {
            workHandler.async(true)
        }
        activityCreatedDestroyed.add(a.toString())
    }

    override fun onActivityStarted(a: Activity?) {}

    @CallSuper
    override fun onActivityResumed(a: Activity?) {
        activityResumePause.add(a.toString())
    }

    @CallSuper
    override fun onActivityPaused(a: Activity?) {
        activityResumePause.remove(a.toString())
        if (activityResumePause.size == 0) {
            workHandler.async(false)
        }
    }

    override fun onActivityStopped(a: Activity?) {}
    override fun onActivitySaveInstanceState(a: Activity?, b: Bundle?) {}

    @CallSuper
    override fun onActivityDestroyed(a: Activity?) {
        activityCreatedDestroyed.remove(a.toString())
    }

    private val internetStateReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                val networkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected

                ReceiverBus.notify(getInternetStateCode(), networkAvailable)
            }
        }
    }
}

internal class WorkHandler(app: BaseApp) : Handler(Utils.newWorkerLooper("AppWorkerHandler", Thread.MAX_PRIORITY)) {

    private val WHAT_ASYNC_INIT = 0xA
    private val WHAT_ASYNC_EXIT = 0xB

    private val reference = WeakReference<BaseApp>(app)

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            WHAT_ASYNC_INIT -> reference.get()?.onInit()
            WHAT_ASYNC_EXIT -> reference.get()?.onExit()

            else            -> throw IllegalStateException("Unknown message type: $msg")
        }
    }

    fun async(init: Boolean)
            = super.sendEmptyMessage(if (init) WHAT_ASYNC_INIT else WHAT_ASYNC_EXIT)
}