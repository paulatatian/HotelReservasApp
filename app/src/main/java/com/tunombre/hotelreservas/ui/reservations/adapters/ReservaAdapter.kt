package com.tunombre.hotelreservas.ui.reservations.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tunombre.hotelreservas.data.models.Reserva
import com.tunombre.hotelreservas.databinding.ItemReservaBinding
import com.tunombre.hotelreservas.utils.DateUtils

class ReservaAdapter : ListAdapter<Reserva, ReservaAdapter.ReservaViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val binding = ItemReservaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReservaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = getItem(position)
        holder.bind(reserva)
    }

    inner class ReservaViewHolder(
        private val binding: ItemReservaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reserva: Reserva) {
            binding.apply {
                textNumeroConfirmacion.text = "Confirmación: ${reserva.numeroConfirmacion}"
                textHabitacion.text = "Habitación ${reserva.numeroHabitacion} - ${reserva.tipoHabitacion}"
                textFechas.text = DateUtils.formatDateRange(reserva.fechaEntrada, reserva.fechaSalida)
                textPrecioTotal.text = "Total: $${reserva.precioTotal}"
                textEstado.text = reserva.getEstado()

                // Color según estado
                val color = when (reserva.getEstado()) {
                    "Confirmada" -> android.graphics.Color.BLUE
                    "En curso" -> android.graphics.Color.GREEN
                    else -> android.graphics.Color.GRAY
                }
                textEstado.setTextColor(color)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reserva>() {
        override fun areItemsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reserva, newItem: Reserva): Boolean {
            return oldItem == newItem
        }
    }
}