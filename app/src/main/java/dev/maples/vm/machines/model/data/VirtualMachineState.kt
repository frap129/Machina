package dev.maples.vm.machines.model.data

sealed class VirtualMachineState {
    object Starting : VirtualMachineState()
    object Ready : VirtualMachineState()
    object Stopped : VirtualMachineState()
}
