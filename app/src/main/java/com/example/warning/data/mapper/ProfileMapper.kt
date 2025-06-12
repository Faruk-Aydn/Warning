package com.example.warning.data.mapper

import android.R.attr.phoneNumber
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.local.entity.ProfileWithContacts
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Profile

fun ProfileWithContacts.toDomain(): Profile {
    return Profile(
        phoneNumber = profile.phoneNumber,
        name = profile.name,
        emergencyMessage = profile.emergencyMessage,
        contacts = contacts.map {
            Contact(
                phoneNumber = it.phoneNumber,
                name = it.name.toString(),
                ownerPhoneNumber = it.ownerPhoneNumber.toString()
            )
        }
    )
}

fun UserDto.toEntity() :ProfileEntity{
    return ProfileEntity(
        name = this.name,
        phoneNumber = this.phoneNumber,
        emergencyMessage = this.emergencyMessage,
    )
}

fun ContactDto.toEntity(): ContactEntity{
    return ContactEntity(
        name = this.name.toString(),
        phoneNumber = this.phoneNumber,
        ownerPhoneNumber = this.ownerPhoneNumber.toString()
    )
}

// Entity -> DTO
fun ProfileEntity.toDTO(contact: List<ContactEntity?>): UserDto {
    return UserDto(
        name = this.name,
        phoneNumber = this.phoneNumber,
        emergencyMessage = this.emergencyMessage,
        contact = contact.map { it?.toDTO() }
    )
}

fun ContactEntity.toDTO(): ContactDto{
    return ContactDto(
        name = this.name,
        phoneNumber = this.phoneNumber,
        ownerPhoneNumber = this.ownerPhoneNumber
    )
}
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
        name = this.name.toString(),
        ownerPhoneNumber = this.ownerPhoneNumber.toString()
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
        name = this.name,
        ownerPhoneNumber = this.ownerPhoneNumber
    )
}

// DOMAIN -> UI STATE