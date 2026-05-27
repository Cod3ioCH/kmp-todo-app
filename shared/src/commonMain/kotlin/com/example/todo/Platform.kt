package com.example.todo

// expect/actual ist das zentrale KMP-Konzept für plattformspezifischen Code.
// Jede Plattform liefert ihre eigene `actual`-Implementierung.
expect class Platform() {
    val name: String
}

fun currentPlatform(): Platform = Platform()
