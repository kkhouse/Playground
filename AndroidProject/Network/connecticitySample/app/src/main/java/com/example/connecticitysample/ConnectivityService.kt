package com.example.connecticitysample

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

interface NetworkConnectivityService {
    val networkStatus: Flow<NetworkStatus>
}

class NetworkConnectivityServiceImpl(
    context: Context,
    request: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build(),
): NetworkConnectivityService  {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Connected)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Disconnected)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Disconnected)
            }
        }
        connectivityManager.registerNetworkCallback(request, connectivityCallback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }
    }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

}