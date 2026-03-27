package com.hakankuru.yanimda.presentation.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val SIGN_IN = "signIn"
    const val SIGN_UP = "signUp"

    // Profil ve ayarlar
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    // Bağlantılar / kişiler
    const val CONTACTS = "contacts"
    const val ADD_CONTACT = "addContact"

    // Bildirimler
    const val NOTIFICATIONS = "notifications"

    // Acil durum geçmişi
    const val EMERGENCY_HISTORY_ROUTE = "emergencyHistory?filterType={filterType}"
    fun emergencyHistory(filterType: String = "ALL") = "emergencyHistory?filterType=$filterType"

    const val INCOMING_DETAIL = "incomingDetail/{messageId}/{currentUserId}"
    fun incomingDetail(messageId: String, currentUserId: String) = "incomingDetail/$messageId/$currentUserId"

    const val OUTGOING_DETAIL = "outgoingDetail/{messageId}/{currentUserId}"
    fun outgoingDetail(messageId: String, currentUserId: String) = "outgoingDetail/$messageId/$currentUserId"
}