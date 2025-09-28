package com.example.warning.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey var id: String,
    var ownerPhone: String,
    var profilePhoto: String?,
    var name: String,                                           //      *
    val country: String,                                        //          *
    val phone: String,    // İletişim kişisinin numarası           //              *      owner ekleyen kişi - mmesaj gönderecek olan
                              //              *      default eklenen kişi - kime gidecek
    var specielMessage: String?= null,                          //          *
    var isLocationSend: Boolean = false,                        //      *
    var tag: String? = null,                                    //  *
    var isTop: Boolean= false ,
    val date: Long,
    var isConfirmed: Boolean
//*
)
