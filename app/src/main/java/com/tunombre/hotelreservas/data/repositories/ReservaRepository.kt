package com.tunombre.hotelreservas.data.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Reserva

class ReservaRepository {

    fun crearReserva(reserva: Reserva, callback: (Boolean, String?) -> Unit) {
        FirebaseManager.database.getReference("reservas").child(reserva.id)
            .setValue(reserva)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

    fun getReservasPorUsuario(usuarioId: String, callback: (List<Reserva>) -> Unit) {
        FirebaseManager.database.getReference("reservas")
            .orderByChild("usuarioId")
            .equalTo(usuarioId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val reservas = mutableListOf<Reserva>()
                    for (child in snapshot.children) {
                        val reserva = child.getValue<Reserva>()
                        reserva?.let { reservas.add(it) }
                    }
                    reservas.sortByDescending { it.fechaEntrada }
                    callback(reservas)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
}