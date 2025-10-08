package com.example.warning.data.remote.Dto

import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName

@Keep
data class ContactDto(
    var id: String = "",
    var phone: String = "",
    var country: String = "",
    var name: String = "",
    var profilePhoto: String? = null,
    var ownerProfilePhoto: String? = null,
    var ownerPhone: String = "",
    var ownerCountry: String = "",
    var ownerName: String = "",
    // Foreign-key like references: who added and who was added
    var addingId: String? = null, // ekleyen (adder)
    var addedId: String? = null,  // eklenen (added/contacted user). Set upon confirmation
    @get:PropertyName("isActiveUser") @set:PropertyName("isActiveUser")
    var isActiveUser: Boolean = false,
    var specialMessage: String? = null,
    @get:PropertyName("isLocationSend") @set:PropertyName("isLocationSend")
    var isLocationSend: Boolean = false,
    var tag: String? = null,
    @get:PropertyName("isTop") @set:PropertyName("isTop")
    var isTop: Boolean = false,
    @get:PropertyName("isConfirmed") @set:PropertyName("isConfirmed")
    var isConfirmed: Boolean = false,
    var date: Long = 0L
)