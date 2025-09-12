package com.example.nfcapp.model

sealed class NfcAction {
    data class OpenUrl(val url: String) : NfcAction()
    object ToggleFlashlight : NfcAction()
    data class GoogleHome(val device: String, val command: String) : NfcAction()
}
