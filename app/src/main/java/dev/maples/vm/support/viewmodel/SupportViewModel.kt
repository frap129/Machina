package dev.maples.vm.support.viewmodel

import androidx.lifecycle.ViewModel
import dev.maples.vm.support.model.repo.SupportRepository

class SupportViewModel(private val supportRepository: SupportRepository) : ViewModel() {
    val supportsVirtualization = supportRepository.featureVirtualizationFrameworkState
}
