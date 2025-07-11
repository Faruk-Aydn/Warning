package com.example.warning.data.mapper

import android.R.attr.phoneNumber
import androidx.room.TypeConverter
import com.example.warning.data.remote.Dto.UserDto
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.local.entity.LinkedEntity
import com.example.warning.data.local.entity.ProfileEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.LinkedDto
import com.example.warning.domain.model.Contact
import com.example.warning.domain.model.Linked
import com.example.warning.domain.model.Profile
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlin.contracts.Returns

// Entity -> DTO
 // gerek yok gibi


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
        locationPermission = locationPermission,
        country = country
    )
}

fun ContactEntity.toDomain(): Contact {
    return Contact(
        name = name,
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop,
        phoneNumber = phone,
        country = country,
        ownerPhoneNumber = ownerPhone,
        profilePhoto = profilePhoto,
        ownerCountry = ownerCountry,
        ownerName = ownerName,
        ownerPhoto = ownerPhoto
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
        locationPermission = locationPermission,
        contactPermission = contactPermission
        // contacts entity'leri ayrı olarak insert edilecek
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        phone = phoneNumber,
        name = name,
        ownerPhoneNumber = ownerPhoneNumber,
        nickName = nickname,
        isActiveUser = isActiveUser,
        specielMessage = specielMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop,
        id = TODO(),
        ownerName = TODO(),
        ownerPhone = TODO(),
        ownerCountry = TODO(),
        ownerPhoto = TODO(),
        profilePhoto = TODO(),
        country = TODO()
    )
}

// contactDto to linkedDto
fun ContactDto.toLinked(): LinkedDto{
    return LinkedDto(
        id = id,
        phone = phone,
        country = country,
        name = name,
        profilePhoto = profilePhoto,
        ownerPhone = ownerPhone,
        date = date
    )
}
// Domain to Dto

fun ContactDto.toEntity(): ContactEntity{
    return ContactEntity(
        id = TODO(),
        ownerName = ownerName,
        ownerPhone = ownerPhone,
        ownerCountry = ownerCountry,
        ownerPhoto = ownerProfilePhoto,
        profilePhoto = profilePhoto,
        name = name,
        country = country,
        phone = phone,
        specielMessage = specialMessage,
        isLocationSend = isLocationSend,
        tag = tag,
        isTop = isTop
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
        isContactPermission = contactPermission,
        linked = null,
        contact = null,
        messageReceiver = null,
        messageSented = null
    )
}

//Dto to Domain

fun LinkedDto.toEntity(): LinkedEntity{
    return LinkedEntity(
        id = id,
        phone = phone,
        country = country,
        name = name,
        profilePhoto = profilePhoto,
        date = date
    )
}
fun UserDto.toEntity(): ProfileEntity{
    return ProfileEntity(
        phone = phoneNumber,
        country = country,
        name = name,
        emergencyMessage = emergencyMessage,
        locationPermission = isLocationPermission,
        contactPermission = isContactPermission,
        linked = fromStringList(linked),
        contact = fromStringList(contact),
        messageReceiver =fromStringList(messageReceiver) ,
        messageSented = fromStringList(messageSented)
    )
}

@TypeConverter
fun fromStringList(value: List<Int?>): String? {

    val gson = Gson()
    return gson.toJson(value)
}

@TypeConverter
fun toStringList(value: String?): List<String?> {

    val gson = Gson()
    if (value == null) return emptyList()
    val listType = object : TypeToken<List<String>>() {}.type
    return gson.fromJson(value, listType)
}
