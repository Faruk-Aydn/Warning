package com.example.warning.data.network

interface ConnectivityObserver {
    fun isOnline(): Boolean
}