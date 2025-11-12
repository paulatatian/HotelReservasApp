package com.tunombre.hotelreservas.ui.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tunombre.hotelreservas.data.models.Habitacion
import com.tunombre.hotelreservas.databinding.FragmentRoomDetailBinding

class RoomDetailFragment : Fragment() {

    private var _binding: FragmentRoomDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitacion: Habitacion

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitacion = arguments?.getParcelable("habitacion") ?: run {
            requireActivity().onBackPressed()
            return
        }

        mostrarDetallesHabitacion(habitacion)
        setupListeners(habitacion)
    }

    private fun mostrarDetallesHabitacion(habitacion: Habitacion) {
        binding.apply {
            textNumeroHabitacion.text = "Habitación ${habitacion.numero}"
            textTipoHabitacion.text = habitacion.getTipoDisplay()
            textPrecio.text = "$${habitacion.precio} por noche"
            textDescripcion.text = habitacion.descripcion

            val caracteristicas = when (habitacion.tipo) {
                "sencilla" -> "• Cama individual\n• Baño privado\n• TV\n• Aire acondicionado\n• WiFi gratis"
                "doble" -> "• Cama doble\n• Baño privado\n• TV\n• Aire acondicionado\n• WiFi gratis\n• Mini nevera"
                else -> "Características no disponibles"
            }
            textCaracteristicas.text = caracteristicas
        }
    }

    private fun setupListeners(habitacion: Habitacion) {
        binding.btnReservar.setOnClickListener {
            val fragment = CreateReservationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("habitacion", habitacion)
                }
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}