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

fun ProfileEntity.toDomain(): Profile{
    return Profile(
        phoneNumber = phone,
        country = country,
        profilePhoto = profilePhoto,
        name = name,
        emergencyMessage = emergencyMessage,
        locationPermission = locationPermission,
        id = id
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
        isConfirmed = isConfirmed
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
        phone = phone,
        country = country,
        name = name,
        profilePhoto = profilePhoto,
        ownerPhone = ownerPhone,
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
        ownerCountry = ownerCountry
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
        id = id.toString()
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
        profilePhoto = profilePhoto.toString(),
        id = id.toString()
    )
}
