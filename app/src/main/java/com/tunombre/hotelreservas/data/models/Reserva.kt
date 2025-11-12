package com.tunombre.hotelreservas.data.models

import java.util.UUID

data class Reserva(
    val id: String = UUID.randomUUID().toString(),
    val usuarioId: String = "",
    val habitacionId: String = "",
    val numeroHabitacion: Int = 0,
    val tipoHabitacion: String = "",
    val fechaEntrada: Long = 0,
    val fechaSalida: Long = 0,
    val precioTotal: Double = 0.0,
    val nombreHuesped: String = "",
    val telefono: String = "",
    val numeroConfirmacion: String = "",
    val fechaCreacion: Long = System.currentTimeMillis()
) {
    fun getEstado(): String {
        val now = System.currentTimeMillis()
        return when {
            now < fechaEntrada -> "Confirmada"
            now in fechaEntrada..fechaSalida -> "En curso"
            else -> "Completada"
        }
    }
}