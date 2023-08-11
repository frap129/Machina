package dev.maples.vm.machines.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dev.maples.vm.machines.model.repo.MachineRepository
import dev.maples.vm.main.launchInBackground
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest

data class MachineScreenState(
    val vmReady: Boolean = false,
    val vmRunning: Boolean = false
)

class MachineViewModel(private val machineRepository: MachineRepository) : ViewModel() {
    private val virtualMachine = machineRepository.virtualMachine

    private val _machineState = MutableStateFlow(MachineScreenState())
    val machineState = _machineState

    lateinit var consoleTextState: State<String>
    lateinit var shellTextState: State<String>

    init {
        launchInBackground {
            virtualMachine.collectLatest {
                if (it != null) {
                    consoleTextState = it.consoleTextState
                    shellTextState = it.shellTextState
                    _machineState.value = MachineScreenState(vmReady = true)
                }
            }
        }
    }

    fun startVirtualMachine() {
        virtualMachine.value?.start()
        _machineState.value = MachineScreenState(
            vmReady = true,
            vmRunning = true
        )
    }

    fun stopVirtualMachine() {
        virtualMachine.value?.stop()
        _machineState.value = MachineScreenState(
            vmReady = true,
            vmRunning = false
        )
    }

    fun sendCommand(cmd: String) = virtualMachine.value?.sendCommand(cmd)
}
