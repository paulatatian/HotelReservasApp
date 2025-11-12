package com.tunombre.hotelreservas.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tunombre.hotelreservas.R
import com.tunombre.hotelreservas.databinding.ActivityMainBinding
import com.tunombre.hotelreservas.ui.rooms.RoomsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cargar el fragmento principal
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RoomsFragment())
                .commit()
        }
    }
}