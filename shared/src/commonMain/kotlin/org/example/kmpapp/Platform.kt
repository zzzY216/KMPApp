package org.example.kmpapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform