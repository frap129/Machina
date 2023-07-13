package dev.maples.vm

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MachinaApplication: Application() {
    private val appModule = module {
        single<MachinaApplication> { this@MachinaApplication }
    }

    override fun onCreate() {
        super.onCreate()

        // Set up Timber
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Timber.plant(NullTree)
        }
    }

    override fun attachBaseContext(base: Context) {
        startKoin {
            androidContext(base)
            modules(appModule)
        }
        super.attachBaseContext(base)
    }

    private object NullTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
           // Do nothing
        }
    }
}