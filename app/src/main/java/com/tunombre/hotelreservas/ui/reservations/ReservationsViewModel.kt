package com.tunombre.hotelreservas.ui.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunombre.hotelreservas.data.models.Reserva
import com.tunombre.hotelreservas.data.repositories.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservationsViewModel : ViewModel() {

    private val reservaRepository = ReservaRepository()

    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _reservaCreada = MutableStateFlow<Reserva?>(null)
    val reservaCreada: StateFlow<Reserva?> = _reservaCreada

    fun cargarReservas(usuarioId: String) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            reservaRepository.getReservasPorUsuario(usuarioId) { reservasList ->
                _reservas.value = reservasList
                _loading.value = false
            }
        }
    }

    fun crearReserva(reserva: Reserva) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            reservaRepository.crearReserva(reserva) { success, errorMessage ->
                _loading.value = false
                if (success) {
                    _reservaCreada.value = reserva
                } else {
                    _error.value = errorMessage ?: "Error al crear la reserva"
                }
            }
        }
    }

    fun limpiarReservaCreada() {
        _reservaCreada.value = null
    }
}