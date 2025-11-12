package com.tunombre.hotelreservas.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseManager {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    init {
        database.setPersistenceEnabled(true)
    }
}