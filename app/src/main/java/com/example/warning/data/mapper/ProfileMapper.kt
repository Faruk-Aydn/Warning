package com.example.warning.data.mapper

import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile
import com.example.warning.domain.model.ProfileUiState

// ENTITY -> DOMAIN

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        phoneNumber = this.phoneNumber,
        name = this.name,
        emergencyMessage = this.emergencyMessage,
        // contacts ayrı tutulduğu için burada boş liste bırakıyoruz,
        // Repository katmanında contacts set edilecek
        contacts = emptyList()
    )
}

fun ContactEntity.toDomain(): Contact {
    return Contact(
        phoneNumber = this.phoneNumber,
        name = this.name
    )
}

// DOMAIN -> ENTITY

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        phoneNumber = this.phoneNumber,
        name = this.name,
        emergencyMessage = this.emergencyMessage
        // contacts entity'leri ayrı olarak insert edilecek
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        phoneNumber = this.phoneNumber,
        name = this.name
    )
}

// DOMAIN -> UI STATE

fun Profile.toUiState(): ProfileUiState {
    return ProfileUiState(
        phoneNumber = this.phoneNumber,
        name = this.name,
        emergencyMessage = this.emergencyMessage,
        approvedContacts = this.contacts.map { contact ->
            Contact(
                phoneNumber = contact.phoneNumber,
                name = contact.name
            )
        }
    )
}
