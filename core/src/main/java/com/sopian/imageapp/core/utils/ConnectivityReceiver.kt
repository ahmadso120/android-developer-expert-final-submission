package com.sopian.imageapp.core.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ConnectivityReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(isConnectedOrConnecting(context!!))
        }
    }

    private fun isConnectedOrConnecting(context: Context): Boolean {
        val checkConnection = CheckConnection(context)
        return checkConnection.isConnected
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}