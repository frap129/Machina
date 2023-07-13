package dev.maples.vm.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.maples.vm.BuildConfig
import dev.maples.vm.IShellProxyService
import dev.maples.vm.services.MachinaService
import dev.maples.vm.services.ShellProxyService
import dev.maples.vm.ui.theme.MachinaTheme
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider


private const val MANAGE_VM = "android.permission.MANAGE_VIRTUAL_MACHINE"
private const val CUSTOM_VM = "android.permission.USE_CUSTOM_VIRTUAL_MACHINE"
private const val SHIZUKU_REQUEST = 0

class MainActivity : ComponentActivity() {
    private lateinit var mMachinaService: MachinaService
    private var mMachinaBound: Boolean = false
    private val mMachinaServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MachinaService.MachinaServiceBinder
            mMachinaService = binder.getService()
            mMachinaBound = true
            mMachinaService.startVirtualMachine()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mMachinaBound = false
        }
    }

    private lateinit var mShellProxyService: IShellProxyService
    private var mShellProxyServiceBound: Boolean = false
    private val mShellProxyServiceArgs = Shizuku.UserServiceArgs(
        ComponentName(BuildConfig.APPLICATION_ID, ShellProxyService::class.java.name))
        .processNameSuffix("user_service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)
    private val mShellProxyServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            mShellProxyService = IShellProxyService.Stub.asInterface(service)
            mShellProxyServiceBound = true
            // Start MachinaService
            Intent(this@MainActivity, MachinaService::class.java).also { intent ->
                bindService(
                    intent,
                    mMachinaServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mShellProxyServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MachinaTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }

        // Setup Shizuku
        val shizukuGranted = if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            checkSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
        } else {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        }

        Shizuku.addRequestPermissionResultListener { _, grantResult ->
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                if (Shizuku.getVersion() < 10) {
                    //Tell the user to upgrade Shizuku.
                } else {
                }
            } else {
                onPermissionDenied()
            }
        }

        when (shizukuGranted) {
            true -> Shizuku.bindUserService(mShellProxyServiceArgs, mShellProxyServiceConnection)
            false -> Shizuku.requestPermission(SHIZUKU_REQUEST)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unbind Services
        unbindService(mMachinaServiceConnection)
        if  (Shizuku.getVersion() >= 10) {
            Shizuku.unbindUserService(mShellProxyServiceArgs, mShellProxyServiceConnection, true);
        }
    }

    private fun grantVirtualMachinePermissions() {
        // Grant permissions
        mShellProxyService.grantPermission(MANAGE_VM)
        try {
            mShellProxyService.grantPermission(CUSTOM_VM)
        } catch (e: Exception) {
            // System is using older version of virtualizationservice
        }

        // Verify permissions are granted
        val manageIsGranted = checkSelfPermission(MANAGE_VM) == PackageManager.PERMISSION_GRANTED
        val customIsGranted = try {
            checkSelfPermission(CUSTOM_VM) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            true
        }

        when(manageIsGranted && customIsGranted) {
            true -> onVirtualMachinePermissionsGranted()
            false -> onPermissionDenied()
        }
    }

    private fun onPermissionDenied() {
        // handle permission denials
    }

    private fun onVirtualMachinePermissionsGranted() {

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MachinaTheme {
        Greeting("Android")
    }
}