package com.example.connecticitysample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

// NOTE：NOTE: Should be DI
class NetworkViewModel(context: Context): ViewModel() {
    val networkStatus = NetworkConnectivityServiceImpl(context).networkStatus.stateIn(
        initialValue = NetworkStatus.Unknown,
        scope = viewModelScope,
        started = WhileSubscribed(5000)
    )
}