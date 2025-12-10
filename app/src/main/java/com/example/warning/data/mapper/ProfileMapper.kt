package com.example.warning.data.mapper

import android.R.attr.phoneNumber
import androidx.room.TypeConverter
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.IncomingEmergencyEntity
import com.example.warning.data.local.entity.LinkedEntity
import com.example.warning.data.local.entity.OutgoingEmergencyEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.EmergencyHistoryDto
import com.example.warning.data.remote.Dto.LinkedDto
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.google.android.play.core.integrity.ad
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlin.contracts.Returns

// Entity -> DTO
 // gerek yok gibi

// DTO -> Gelen Kutusu Entity
fun EmergencyHistoryDto.toIncomingEntity(): IncomingEmergencyEntity {
    return IncomingEmergencyEntity(
        id = this.id,
        senderId = this.senderId,
        senderName = this.senderName ?: "Bilinmiyor",
        messageContent = this.messageContent,
        latitude = this.latitude,
        longitude = this.longitude,
        date = this.timestamp?.time ?: System.currentTimeMillis()
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
        date = this.timestamp?.time ?: System.currentTimeMillis()
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
fun UserDto.toDomain(): Profile {
    return Profile(
        id = id,
        phoneNumber = phoneNumber ?: "",
        country = country ?: "",
        name = name ?: "",
        profilePhoto = profilePhoto,
        emergencyMessage = emergencyMessage,
        locationPermission = isLocationPermission ?: false,
        fcmToken = fcmToken // YENİ: DTO'dan Domain'e
    )
}