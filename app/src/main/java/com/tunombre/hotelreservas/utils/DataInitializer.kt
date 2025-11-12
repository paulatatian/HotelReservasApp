package com.tunombre.hotelreservas.utils

import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Habitacion

object DataInitializer {

    fun initializeRooms() {
        val habitaciones = mutableListOf<Habitacion>()

        for (i in 1..12) {
            habitaciones.add(
                Habitacion(
                    id = "sencilla_$i",
                    numero = i,
                    tipo = "sencilla",
                    precio = 80.0,
                    descripcion = "Habitación sencilla con cama individual, baño privado y todas las comodidades."
                )
            )
        }

        for (i in 13..20) {
            habitaciones.add(
                Habitacion(
                    id = "doble_${i-12}",
                    numero = i,
                    tipo = "doble",
                    precio = 120.0,
                    descripcion = "Habitación doble con cama matrimonial, baño privado y amenities premium."
                )
            )
        }

        // CORRECCIÓN: Usar getReference() o reference directamente
        val databaseRef = FirebaseManager.database.reference

        habitaciones.forEach { habitacion ->
            databaseRef.child("habitaciones").child(habitacion.id).setValue(habitacion)
        }
    }
}