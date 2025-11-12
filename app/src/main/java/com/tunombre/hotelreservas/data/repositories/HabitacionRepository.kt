package com.tunombre.hotelreservas.data.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Habitacion

class HabitacionRepository {

    fun getHabitaciones(callback: (List<Habitacion>) -> Unit) {
        FirebaseManager.database.getReference("habitaciones")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val habitaciones = mutableListOf<Habitacion>()
                    for (child in snapshot.children) {
                        val habitacion = child.getValue<Habitacion>()
                        habitacion?.let { habitaciones.add(it) }
                    }
                    callback(habitaciones)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    fun getHabitacionesDisponibles(fechaEntrada: Long, fechaSalida: Long, callback: (List<Habitacion>) -> Unit) {
        FirebaseManager.database.getReference("reservas")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val habitacionesOcupadas = mutableSetOf<String>()

                    for (reservaSnapshot in snapshot.children) {
                        val reserva = reservaSnapshot.getValue<com.tunombre.hotelreservas.data.models.Reserva>()
                        reserva?.let {
                            if (hayConflictoFechas(it.fechaEntrada, it.fechaSalida, fechaEntrada, fechaSalida)) {
                                habitacionesOcupadas.add(it.habitacionId)
                            }
                        }
                    }

                    getHabitaciones { todasHabitaciones ->
                        val disponibles = todasHabitaciones.filter {
                            it.id !in habitacionesOcupadas
                        }
                        callback(disponibles)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    private fun hayConflictoFechas(entrada1: Long, salida1: Long, entrada2: Long, salida2: Long): Boolean {
        return entrada1 < salida2 && salida1 > entrada2
    }
}