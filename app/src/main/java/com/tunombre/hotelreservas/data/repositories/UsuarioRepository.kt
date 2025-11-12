package com.tunombre.hotelreservas.data.repositories

import com.google.firebase.database.ktx.getValue
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Usuario

class UsuarioRepository {

    fun crearUsuario(usuario: Usuario, callback: (Boolean) -> Unit) {
        FirebaseManager.database.getReference("usuarios").child(usuario.id)
            .setValue(usuario)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUsuario(userId: String, callback: (Usuario?) -> Unit) {
        FirebaseManager.database.getReference("usuarios").child(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val usuario = snapshot.getValue<Usuario>()
                callback(usuario)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}