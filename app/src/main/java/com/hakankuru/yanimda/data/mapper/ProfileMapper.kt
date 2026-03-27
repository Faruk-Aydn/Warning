package com.hakankuru.yanimda.data.mapper

import com.hakankuru.yanimda.data.remote.Dto.UserDto
import com.hakankuru.yanimda.data.local.entity.ContactEntity
import com.hakankuru.yanimda.data.local.entity.IncomingEmergencyEntity
import com.hakankuru.yanimda.data.local.entity.LinkedEntity
import com.hakankuru.yanimda.data.local.entity.OutgoingEmergencyEntity
import com.hakankuru.yanimda.data.local.entity.ProfileEntity
import com.hakankuru.yanimda.data.remote.Dto.ContactDto
import com.hakankuru.yanimda.data.remote.Dto.EmergencyHistoryDto
import com.hakankuru.yanimda.data.remote.Dto.LinkedDto
import com.hakankuru.yanimda.domain.model.Contact
import com.hakankuru.yanimda.domain.model.Linked
import com.hakankuru.yanimda.domain.model.Profile

// Entity -> DTO
 // gerek yok gibi

// DTO -> Gelen Kutusu Entity
fun EmergencyHistoryDto.toIncomingEntity(): IncomingEmergencyEntity {
    return IncomingEmergencyEntity(
        id = this.id,
        senderId = this.senderId,
        senderName = this.senderName ?: "Bilinmiyor",
        messageContent = this.messageContent,
        latitude = this.location?.lat,
        longitude = this.location?.lng,
        date = this.timestamp?.time ?: System.currentTimeMillis(),
        isLocationSent = this.locationSent
    )
}

// DTO -> Giden Kutusu Entity
fun EmergencyHistoryDto.toOutgoingEntity(): OutgoingEmergencyEntity {
    return OutgoingEmergencyEntity(
        id = this.id,
        receiverId = this.receiverId,
        receiverName = this.receiverName,
        messageContent = this.messageContent,
        isLocationSent = this.locationSent,
        status = this.status,
        success = this.success,
        error = this.error,
        date = this.timestamp?.time ?: System.currentTimeMillis(),
        latitude = location?.lat,
        longitude = location?.lng
    )
}
// ENTITY -> DOMAIN

fun ProfileEntity.toDomain(): Profile{
    return Profile(
        phoneNumber = phone,
        country = country,
        profilePhoto = profilePhoto,
        name = name,
        emergencyMessage = emergencyMessage,
        locationPermission = locationPermission,
        id = id // Bu Firestore document ID'si
    )
}

fun ContactEntity.toDomain(): Contact {
    return Contact(
        id = id,
        name = name.toString(),
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop,
        phoneNumber = phone,
        country = country,
        ownerPhoneNumber = ownerPhone,
        profilePhoto = profilePhoto,
        isConfirmed = isConfirmed,
        addedId = addedId,
        addingId =addingId
    )
}

fun LinkedEntity.toDomain(): Linked{
    return Linked(
        phoneNumber = phone,
        name = name,
        country = country,
        profilePhoto = profilePhoto,
        ownerPhoneNumber = ownerPhone,
        date = date,
        id = id,
        isConfirmed = isConfirmed
    )
}
// DOMAIN -> ENTITY

// contactDto to linkedDto
fun ContactDto.toLinked(): LinkedDto{
    return LinkedDto(
        id = id,
        phone = ownerPhone,
        country = ownerCountry,
        name = ownerName,
        profilePhoto = ownerProfilePhoto,
        ownerPhone = phone,
        date = date,
        isConfirmed = isConfirmed
    )
}
// Domain to Dto

fun ContactDto.toEntity(): ContactEntity{
    return ContactEntity(
        ownerPhone = ownerPhone,
        profilePhoto = profilePhoto,
        name = name,
        country = country,
        phone = phone,
        specielMessage = specialMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop,
        date = date,
        isConfirmed = isConfirmed,
        id = id,
        ownerCountry = ownerCountry,
        addedId = addedId,
        addingId =addingId,
    )

}

fun Profile.toDto(): UserDto{ //sadece kayıt
    return UserDto(
        phoneNumber = phoneNumber,
        country = country,
        profilePhoto = profilePhoto,
        name = name,
        emergencyMessage = emergencyMessage,
        isLocationPermission = locationPermission,
        id = id.toString(),
        fcmToken = fcmToken // YENİ: Domain'den DTO'ya
    )
}

//Dto to Domain

fun LinkedDto.toEntity(): LinkedEntity{
    return LinkedEntity(
        phone = phone,
        country = country,
        name = name,
        profilePhoto = profilePhoto,
        date = date,
        ownerPhone = ownerPhone,
        id = id,
        isConfirmed = isConfirmed
    )
}
fun UserDto.toEntity(): ProfileEntity{
    return ProfileEntity(
        phone = phoneNumber,
        country = country,
        name = name,
        emergencyMessage = emergencyMessage,
        locationPermission = isLocationPermission,
        profilePhoto = profilePhoto ?: "",
        id = id ?: phoneNumber, // Eğer id null ise phoneNumber'ı kullan
        fcmToken = fcmToken
    )
}
