package dev.maples.vm.main

import android.app.Application
import android.content.Context
import dev.maples.vm.BuildConfig
import dev.maples.vm.machines.model.repo.MachineRepository
import dev.maples.vm.machines.viewmodel.MachineViewModel
import dev.maples.vm.permissions.model.repo.PermissionsRepository
import dev.maples.vm.permissions.viewmodel.PermissionsViewModel
import dev.maples.vm.preferences.model.repo.PreferencesRepository
import dev.maples.vm.support.model.repo.SupportRepository
import dev.maples.vm.support.viewmodel.SupportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MachinaApplication : Application() {
    private val appModule = module {
        single<MachinaApplication> { this@MachinaApplication }
    }

    private val repoModule = module {
        single<SupportRepository> { SupportRepository(androidContext()) }
        single<PermissionsRepository> { PermissionsRepository(androidContext()) }
        single<PreferencesRepository> { PreferencesRepository(androidContext()) }
        single<MachineRepository> { MachineRepository(androidContext()) }
    }

    private val viewModelModule = module {
        viewModel { SupportViewModel(get()) }
        viewModel { PermissionsViewModel(get()) }
        single<MachineViewModel> { MachineViewModel(get()) }
    }

    override fun attachBaseContext(base: Context) {
        startKoin {
            androidContext(base)
            androidLogger()
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

fun launchInBackground(block: suspend CoroutineScope.() -> Unit) =
    CoroutineScope(Dispatchers.IO).launch(block = block)
