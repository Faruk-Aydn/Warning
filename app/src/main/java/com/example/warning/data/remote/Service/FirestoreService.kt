package com.example.warning.data.remote.Service

import android.util.Log
import android.util.Log.e
import com.example.warning.data.local.entity.ContactEntity
import com.example.warning.data.remote.Dto.ContactDto
import com.example.warning.data.remote.Dto.LinkedDto
import com.example.warning.data.remote.Dto.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.java

class FirestoreService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    //table Linked

    //tableContact


    suspend fun addContact(contactDto: ContactDto): Boolean {
        return try {
            firestore.collection("contacts")
                .document(contactDto.id)
                .set(contactDto)
                .await()
            true
        } catch (e: CancellationException) {
            Log.w("Service", "Coroutine iptal edildi")
            throw e
        } catch (e: Exception) {
            Log.e("ServiceAddContact", "Hata: ${e.message}", e)
            false
        }
    }

    suspend fun updateName(userId: String, newName: String) {
        firestore.collection("profiles")
            .document(userId)
            .update("name", newName)
            .await()
    }
    // table User
    fun uploadUser(userDto: UserDto?, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        if (userDto == null) {
            onError(IllegalArgumentException("userDto is null"))
            return
        }

        firestore.collection("profile")
            .document(userDto.phoneNumber)
            .set(userDto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
    suspend fun isUserRegistered(phoneNumber: String): Boolean {
        return try {
            val snapshot = firestore.collection("profiles")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1) // sadece 1 kayıt yeterli
                .get()
                .await()
            !snapshot.isEmpty // varsa true, yoksa false // kullanıcı varsa true, yoksa false
        } catch (e: Exception) {
            Log.w("Firestore", "Kullanıcı kontrol edilirken hata: $e")
            false
        }
    }


    suspend fun registerUser(userDto: UserDto): Boolean{
        return try {
            firestore.collection("profiles")
                .document(userDto.phoneNumber)
                .set(userDto)
                .await()
            Log.i("addUser", "Başarılı: ${userDto.phoneNumber}") // Bu çalışıyor mu bak
            true // başarılı olursa true döner
        }
        catch (e: CancellationException) {
            Log.w("Service", "Coroutine iptal edildi")
            throw e
        }
        catch (e: Exception) {
            Log.w("Service",e)
            false // hata varsa false döner
        }

    }

    suspend fun getProfile(phoneNumber: String): UserDto? {
        return try {
            val snapshot = firestore.collection("profiles")
                .document(phoneNumber)
                .get()
                .await()

            Log.d("Service", "Firebase'den veri çekildi: ${snapshot.data}")
            return snapshot.toObject(UserDto::class.java)
        }catch (e: CancellationException) {
            // Job iptal edilmişse burası normal, hata gibi göstermemek daha iyi
            Log.d("ServiceGet", "Coroutine iptal edildi.")
            throw e // tekrar fırlat, aksi halde coroutine hiyerarşisi bozulur
        }
        catch (e: Exception) {
            Log.e("ServiceGet", "HATA: ${e.message}", e)
            throw e
        }
    }

    suspend fun getLinked(phone: String): List<LinkedDto> {
        val snapshot = firestore
            .collection("contacts")
            .whereEqualTo("phone", phone)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(ContactDto::class.java)?.let { entity ->
                LinkedDto(
                    id = doc.id,
                    phone = entity.ownerPhone,  // linked tarafı için karşı taraf owner oluyor
                    country = entity.ownerCountry,
                    name = entity.ownerName,
                    profilePhoto = entity.ownerProfilePhoto,
                    ownerPhone = entity.phone,
                    date = entity.date,
                    isConfirmed = entity.isConfirmed
                )
            }
        }
    }

    suspend fun getContacts(ownerPhone: String): List<ContactDto> {
        val snapshot = firestore
            .collection("contacts")
            .whereEqualTo("ownerPhone", ownerPhone)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(ContactDto::class.java)?.let { entity ->
                ContactDto(
                    id = doc.id,
                    phone = entity.phone,
                    country = entity.country,
                    name = entity.name,
                    profilePhoto = entity.profilePhoto,
                    ownerProfilePhoto = entity.ownerProfilePhoto,
                    ownerPhone = entity.ownerPhone,
                    ownerCountry = entity.ownerCountry,
                    ownerName = entity.ownerName,
                    isActiveUser = entity.isActiveUser,
                    specialMessage = entity.specialMessage,
                    isLocationSend = entity.isLocationSend,
                    tag = entity.tag,
                    isTop = entity.isTop,
                    isConfirmed = entity.isConfirmed,
                    date = entity.date
                )
            }
        }
    }
}
