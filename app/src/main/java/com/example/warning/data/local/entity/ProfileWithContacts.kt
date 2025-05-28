package com.example.warning.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileWithContacts(
    @Embedded val profile: ProfileEntity,

    @Relation(
        parentColumn = "phoneNumber",
        entityColumn = "ownerPhoneNumber"
    )
    val contacts: List<ContactEntity>
)
