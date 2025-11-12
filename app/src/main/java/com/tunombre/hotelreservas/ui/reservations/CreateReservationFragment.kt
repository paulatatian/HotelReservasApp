package com.tunombre.hotelreservas.ui.reservations

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Habitacion
import com.tunombre.hotelreservas.data.models.Reserva
import com.tunombre.hotelreservas.databinding.FragmentCreateReservationBinding
import com.tunombre.hotelreservas.utils.DateUtils
import com.tunombre.hotelreservas.utils.Validators
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class CreateReservationFragment : Fragment() {

    private var _binding: FragmentCreateReservationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReservationsViewModel by viewModels()
    private lateinit var habitacion: Habitacion

    private var fechaEntrada: Long = 0
    private var fechaSalida: Long = 0
    private var precioTotal: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitacion = arguments?.getParcelable("habitacion") ?: run {
            requireActivity().onBackPressed()
            return
        }

        mostrarInformacionHabitacion(habitacion)
        setupObservers()
        setupListeners(habitacion)
    }

    private fun mostrarInformacionHabitacion(habitacion: Habitacion) {
        binding.apply {
            textHabitacion.text = "Habitación ${habitacion.numero} - ${habitacion.getTipoDisplay()}"
            textPrecioNoche.text = "Precio por noche: $${habitacion.precio}"
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.reservaCreada.collect { reserva ->
                reserva?.let {
                    mostrarConfirmacionReserva(reserva)
                    viewModel.limpiarReservaCreada()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnConfirmar.isEnabled = !loading
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners(habitacion: Habitacion) {
        binding.btnSelectEntrada.setOnClickListener {
            showDatePicker(true, habitacion.precio)
        }

        binding.btnSelectSalida.setOnClickListener {
            showDatePicker(false, habitacion.precio)
        }

        binding.btnConfirmar.setOnClickListener {
            if (validarFormulario() && fechaEntrada > 0 && fechaSalida > 0) {
                crearReserva(habitacion)
            }
        }
    }

    private fun showDatePicker(isEntrada: Boolean, precioNoche: Double) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            val timestamp = selectedCalendar.timeInMillis

            if (isEntrada) {
                fechaEntrada = timestamp
                binding.btnSelectEntrada.text = DateUtils.formatDate(timestamp)
            } else {
                fechaSalida = timestamp
                binding.btnSelectSalida.text = DateUtils.formatDate(timestamp)
            }

            if (fechaEntrada > 0 && fechaSalida > 0 && fechaEntrada < fechaSalida) {
                val noches = DateUtils.calcularNoches(fechaEntrada, fechaSalida)
                precioTotal = precioNoche * noches
                binding.textPrecioTotal.text = "Total: $${precioTotal} ($noches noches)"
            }
        }, year, month, day)

        datePicker.show()
    }

    private fun validarFormulario(): Boolean {
        val nombre = binding.etNombreHuesped.text.toString()
        val telefono = binding.etTelefono.text.toString()

        if (!Validators.isValidName(nombre)) {
            binding.etNombreHuesped.error = "Nombre no válido"
            return false
        }

        if (!Validators.isValidPhone(telefono)) {
            binding.etTelefono.error = "Teléfono no válido"
            return false
        }

        if (fechaEntrada == 0L || fechaSalida == 0L) {
            android.widget.Toast.makeText(requireContext(), "Selecciona las fechas", android.widget.Toast.LENGTH_LONG).show()
            return false
        }

        if (fechaEntrada >= fechaSalida) {
            android.widget.Toast.makeText(requireContext(), "La fecha de salida debe ser posterior a la de entrada", android.widget.Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun crearReserva(habitacion: Habitacion) {
        val currentUser = FirebaseManager.auth.currentUser
        if (currentUser == null) {
            android.widget.Toast.makeText(requireContext(), "Debes iniciar sesión", android.widget.Toast.LENGTH_LONG).show()
            return
        }

        val reserva = Reserva(
            usuarioId = currentUser.uid,
            habitacionId = habitacion.id,
            numeroHabitacion = habitacion.numero,
            tipoHabitacion = habitacion.tipo,
            fechaEntrada = fechaEntrada,
            fechaSalida = fechaSalida,
            precioTotal = precioTotal,
            nombreHuesped = binding.etNombreHuesped.text.toString(),
            telefono = binding.etTelefono.text.toString(),
            numeroConfirmacion = generarNumeroConfirmacion()
        )

        viewModel.crearReserva(reserva)
    }

    private fun generarNumeroConfirmacion(): String {
        return "HR${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
    }

    private fun mostrarConfirmacionReserva(reserva: Reserva) {
        binding.apply {
            layoutFormulario.visibility = View.GONE
            layoutConfirmacion.visibility = View.VISIBLE

            textConfirmacionNumero.text = "Número de confirmación: ${reserva.numeroConfirmacion}"
            textConfirmacionHabitacion.text = "Habitación: ${reserva.numeroHabitacion} - ${reserva.tipoHabitacion}"
            textConfirmacionFechas.text = "Fechas: ${DateUtils.formatDateRange(reserva.fechaEntrada, reserva.fechaSalida)}"
            textConfirmacionTotal.text = "Total: $${reserva.precioTotal}"
            textConfirmacionHuesped.text = "Huésped: ${reserva.nombreHuesped}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}