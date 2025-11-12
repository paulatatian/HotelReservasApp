package com.tunombre.hotelreservas.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Habitacion(
    val id: String = "",
    val numero: Int = 0,
    val tipo: String = "", // "sencilla" o "doble"
    val precio: Double = 0.0,
    val descripcion: String = "",
    val disponible: Boolean = true
) : Parcelable {
    fun getTipoDisplay(): String {
        return when(tipo) {
            "sencilla" -> "Habitación Sencilla"
            "doble" -> "Habitación Doble"
            else -> tipo
        }
    }
}