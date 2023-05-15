package com.example.connecticitysample

sealed class NetworkStatus {
    object Unknown: NetworkStatus()
    object Connected: NetworkStatus()
    object Disconnected: NetworkStatus()
}