package com.example.warning.data.remote.Dto

data class EmergencyMessageRequestDto(
    val userId: String,
    // Eklenen alan: Acil durum mesajının kendisi
    val emergencyMessage: String,
    // Eklenen alan: Mesajın gönderileceği alıcıların listesi
    val contacts: List<ContactDetailDto>
)

// src/main/java/com/example/warning/data/remote/Dto/ContactDetailDto.kt

data class ContactDetailDto(
    val contactPhoneNumber: String,
    // Alıcının özel mesajı var mı kontrolü için
    val specialMessage: String?
)