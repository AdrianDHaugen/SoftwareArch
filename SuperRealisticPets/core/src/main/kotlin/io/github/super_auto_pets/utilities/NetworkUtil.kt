package io.github.super_auto_pets.utilities

import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtil {
    fun isInternetAvailable(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
