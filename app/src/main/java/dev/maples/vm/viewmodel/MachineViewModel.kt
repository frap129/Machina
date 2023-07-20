package dev.maples.vm.viewmodel

import androidx.lifecycle.ViewModel
import dev.maples.vm.model.repository.MachineRepository

class MachineViewModel(private val machineRepository: MachineRepository) : ViewModel() {
    fun startRootVirtualMachine() = machineRepository.startMachinaService()

    val consoleTextState = machineRepository.consoleTextState
}