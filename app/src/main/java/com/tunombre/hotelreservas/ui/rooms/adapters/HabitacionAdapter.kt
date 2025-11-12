package com.tunombre.hotelreservas.ui.rooms.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tunombre.hotelreservas.data.models.Habitacion
import com.tunombre.hotelreservas.databinding.ItemHabitacionBinding

class HabitacionAdapter(
    private val onItemClick: (Habitacion) -> Unit
) : ListAdapter<Habitacion, HabitacionAdapter.HabitacionViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitacionViewHolder {
        val binding = ItemHabitacionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitacionViewHolder, position: Int) {
        val habitacion = getItem(position)
        holder.bind(habitacion)
    }

    inner class HabitacionViewHolder(
        private val binding: ItemHabitacionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habitacion: Habitacion) {
            binding.apply {
                textNumeroHabitacion.text = "Habitación ${habitacion.numero}"
                textTipoHabitacion.text = habitacion.getTipoDisplay()
                textPrecio.text = "$${habitacion.precio}/noche"
                textDescripcion.text = habitacion.descripcion

                root.setOnClickListener {
                    onItemClick(habitacion)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Habitacion>() {
        override fun areItemsTheSame(oldItem: Habitacion, newItem: Habitacion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habitacion, newItem: Habitacion): Boolean {
            return oldItem == newItem
        }
    }
}