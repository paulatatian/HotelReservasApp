package com.tunombre.hotelreservas.ui.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunombre.hotelreservas.data.models.Habitacion
import com.tunombre.hotelreservas.data.repositories.HabitacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RoomsViewModel : ViewModel() {

    private val habitacionRepository = HabitacionRepository()

    private val _habitaciones = MutableStateFlow<List<Habitacion>>(emptyList())
    val habitaciones: StateFlow<List<Habitacion>> = _habitaciones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarHabitaciones() {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            habitacionRepository.getHabitaciones { habitacionesList ->
                _habitaciones.value = habitacionesList
                _loading.value = false
            }
        }
    }

    fun buscarHabitacionesDisponibles(fechaEntrada: Long, fechaSalida: Long, tipo: String? = null) {
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            habitacionRepository.getHabitacionesDisponibles(fechaEntrada, fechaSalida) { habitacionesList ->
                val habitacionesFiltradas = if (!tipo.isNullOrEmpty()) {
                    habitacionesList.filter { it.tipo == tipo }
                } else {
                    habitacionesList
                }
                _habitaciones.value = habitacionesFiltradas
                _loading.value = false

                if (habitacionesFiltradas.isEmpty()) {
                    _error.value = "No hay habitaciones disponibles para las fechas seleccionadas"
                }
            }
        }
    }
}