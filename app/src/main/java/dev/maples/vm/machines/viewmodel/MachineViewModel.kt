package dev.maples.vm.machines.viewmodel

import androidx.lifecycle.ViewModel
import dev.maples.vm.machines.model.repo.MachineRepository

class MachineViewModel(private val machineRepository: MachineRepository) : ViewModel() {
    fun startRootVirtualMachine() = machineRepository.startVirtualMachine()
    fun stopRootVirtualMachine() = machineRepository.stopVirtualMachine()
    fun sendCommand(cmd: String) = machineRepository.sendCommand(cmd)

    val consoleTextState = machineRepository.consoleTextState
    val shellTextState = machineRepository.shellTextState
}
