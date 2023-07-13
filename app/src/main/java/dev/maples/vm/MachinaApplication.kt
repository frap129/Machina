package dev.maples.vm

import android.app.Application
import android.content.Context
import dev.maples.vm.model.repository.MachineRepository
import dev.maples.vm.model.repository.PermissionsRepository
import dev.maples.vm.model.repository.PreferencesRepository
import dev.maples.vm.viewmodel.MachineViewModel
import dev.maples.vm.viewmodel.PermissionsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MachinaApplication : Application() {
    private val appModule = module {
        single<MachinaApplication> { this@MachinaApplication }
    }

    private val repoModule = module {
        single<PermissionsRepository> { PermissionsRepository(androidContext()) }
        single<PreferencesRepository> { PreferencesRepository(androidContext()) }
        single<MachineRepository> { MachineRepository(androidContext()) }
    }

    private val viewModelModule = module {
        viewModel { PermissionsViewModel(get()) }
        viewModel { MachineViewModel(get()) }
    }

    override fun attachBaseContext(base: Context) {
        startKoin {
            androidContext(base)
            modules(appModule, repoModule, viewModelModule)
        }
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        // Set up Timber
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            false -> Timber.plant(NullTree)
        }
    }

    private object NullTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // Do nothing
        }
    }
}