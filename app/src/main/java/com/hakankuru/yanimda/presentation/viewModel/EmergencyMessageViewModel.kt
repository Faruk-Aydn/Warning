package ccom.hakankuru.yanimda.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.yanimda.domain.model.Stats
import com.hakankuru.yanimda.domain.repository.EmergencyHistoryRepository
import com.hakankuru.yanimda.domain.repository.ProfileRepository
import com.hakankuru.yanimda.domain.usecase.EmergencyState
import com.hakankuru.yanimda.domain.usecase.SendEmergencyMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class EmergencyMessageViewModel @Inject constructor(
    private val sendEmergencyMessageUseCase: SendEmergencyMessageUseCase,
    private val repository: EmergencyHistoryRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _emergencyMessageState = MutableStateFlow<EmergencyState>(EmergencyState.Idle)
    val emergencyMessageState: StateFlow<EmergencyState> = _emergencyMessageState

    val statsState: StateFlow<Stats> = flow {
        val user = profileRepo.getCurrentUserOnce() // [cite: 195]
        val userId = user?.id ?: ""

        if (userId.isNotEmpty()) {
            repository.getAllMessagesForUser(userId).collect { messages -> // [cite: 161]
                val sent = messages.count { it.senderId == userId }
                val received = messages.count { it.receiverId == userId }
                val lastTime = messages.firstOrNull()?.let {
                    formatTimestamp(it.timestampMillis)
                } ?: "-"

                emit(Stats(sent, received, lastTime))
            }
        } else {
            emit(Stats())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Stats()
    )
    fun sendEmergencyMessage() {
        // UI'dan gelen tetikleme: Butona basınca burası çağrılıyor.
        viewModelScope.launch {
            try {
                _emergencyMessageState.value = EmergencyState.Loading

                val result = sendEmergencyMessageUseCase()

                _emergencyMessageState.value = EmergencyState.Success(
                    successCount = result.successCount,
                    failureCount = result.failureCount
                )
            } catch (e: Exception) {
                // Şimdilik basit hata yakalama
                _emergencyMessageState.value =
                    EmergencyState.Error(e.message ?: "Bilinmeyen bir hata oluştu")
            }
        }
    }

    fun resetState() {
        _emergencyMessageState.value = EmergencyState.Idle
    }

    private fun formatTimestamp(timestampMillis: Long): String {
        return try {
            val instant = Instant.ofEpochMilli(timestampMillis)
            val zoneId = ZoneId.systemDefault()
            val dateTime = instant.atZone(zoneId)
            val formatter = DateTimeFormatter.ofPattern("HH:mm") // Sadece saat ve dakika
            dateTime.format(formatter)
        } catch (e: Exception) {
            "--:--"
        }
    }
}