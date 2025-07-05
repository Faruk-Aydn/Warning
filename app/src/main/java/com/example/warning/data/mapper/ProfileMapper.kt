package com.example.warning.data.mapper

import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.LinkedEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.remote.Dto.LinkedDto
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.google.android.play.integrity.internal.n

// Entity -> DTO
fun ProfileEntity.toDTO(
    contact: List<ContactEntity?>,
    linkeds: List<LinkedEntity?>
): UserDto {
    return UserDto(
        name = name,
        phoneNumber = phoneNumber,
        emergencyMessage = emergencyMessage,
        contact = contact.map { it?.toDTO() },
        isLocationPermission = locationPermission,
        ContactPermission = contactPermission,
        linked = linkeds.map { it?.toDto() }
    )
}

fun LinkedEntity.toDto(): LinkedDto{
    return LinkedDto(
        phoneNumber = phoneNumber,
        name = name,
        nickName = nickName,
        ownerPhoneNumber = ownerPhoneNumber,
        date = date
    )
}

fun ContactEntity.toDTO(): ContactDto{
    return ContactDto(
        name = name,
        phoneNumber = phoneNumber,
        ownerPhoneNumber = ownerPhoneNumber,
        nickName = nickName,
        isActiveUser = isActiveUser,
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop
    )
}
// ENTITY -> DOMAIN

fun LinkedEntity.toDomain(): Linked{
    return Linked(
        phoneNumber = phoneNumber,
        name = name,
        nickName = nickName,
        ownerPhoneNumber = ownerPhoneNumber,
        date = date
    )
}

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        phoneNumber = phoneNumber,
        name = name,
        emergencyMessage = emergencyMessage,
        contactPermission =contactPermission,
        locationPermission = locationPermission
    )
}

fun ContactEntity.toDomain(): Contact {
    return Contact(
        phoneNumber = phoneNumber,
        name = name,
        ownerPhoneNumber = ownerPhoneNumber.toString(),
        nickname = nickName,
        isActiveUser = isActiveUser,
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop
    )
}

// DOMAIN -> ENTITY

fun Linked.toEntity(): LinkedEntity{
    return LinkedEntity(
        phoneNumber = phoneNumber,
        name = name,
        nickName = nickName,
        ownerPhoneNumber = ownerPhoneNumber,
        date = date
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        phoneNumber = phoneNumber,
        name = name,
        emergencyMessage = emergencyMessage,
        locationPermission= locationPermission,
        contactPermission= contactPermission
        // contacts entity'leri ayrı olarak insert edilecek
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        phoneNumber = phoneNumber,
        name = name,
        ownerPhoneNumber = ownerPhoneNumber,
        nickName = nickname,
        isActiveUser = isActiveUser,
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop
    )
}
