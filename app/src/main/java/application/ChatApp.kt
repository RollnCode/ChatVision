package application

import android.arch.persistence.room.Room
import android.content.Context
import android.support.multidex.MultiDex
import com.rollncode.basement.application.BaseApp
import instruments.QBHelper
import storage.Settings
import storage.room.RoomDb

/**
 *
 * @author Andrey Turkovsky turkovsky.andrey@gmail.com
 * @since 2017.10.24
 */
class ChatApp : BaseApp() {

    companion object {
        private const val DB_NAME = "ChatVisionDb"
        lateinit var dataBase: RoomDb
        private set
    }

    override fun onCreate() {
        super.onCreate()
        Settings.init(this)
        QBHelper.init(this)
        dataBase = Room.databaseBuilder(this, RoomDb::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onInit() {

    }

    override fun onExit() {

    }

    override fun getInternetStateCode()
            = 0
}