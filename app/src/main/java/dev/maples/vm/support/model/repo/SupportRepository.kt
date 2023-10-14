package dev.maples.vm.support.model.repo

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupportRepository(private val context: Context) {
    /*
        Check FEATURE_VIRTUALIZATION_FRAMEWORK
     */
    private val _featureVirtualizationFrameworkState = MutableStateFlow(
        context.packageManager.hasSystemFeature(FEATURE_VIRTUALIZATION_FRAMEWORK)
    )

    val featureVirtualizationFrameworkState = _featureVirtualizationFrameworkState.asStateFlow()

    private companion object {
        /**
         * Feature for [.getSystemAvailableFeatures] and [.hasSystemFeature].
         * This feature indicates whether device supports
         * [Android Virtualization Framework](https://source.android.com/docs/core/virtualization).
         *
         * @hide
         */
        var FEATURE_VIRTUALIZATION_FRAMEWORK = "android.software.virtualization_framework"
    }
}
