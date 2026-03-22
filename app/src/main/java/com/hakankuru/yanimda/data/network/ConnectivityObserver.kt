package com.hakankuru.yanimda.data.network

interface ConnectivityObserver {
    fun isOnline(): Boolean
}