package dev.maples.vm.viewmodel

import androidx.lifecycle.ViewModel
import dev.maples.vm.model.repository.MachineRepository

class MachineViewModel(private val machineRepository: MachineRepository) : ViewModel() {
    fun startRootVirtualMachine() = machineRepository.startVirtualMachine()
    fun stopRootVirtualMachine() = machineRepository.stopVirtualMachine()
    fun sendCommand(cmd: String) = machineRepository.sendCommand(cmd)

    val consoleTextState = machineRepository.consoleTextState
    val shellTextState = machineRepository.shellTextState
}