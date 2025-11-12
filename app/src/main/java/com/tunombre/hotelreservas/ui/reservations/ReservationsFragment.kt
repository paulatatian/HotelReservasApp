package com.tunombre.hotelreservas.ui.reservations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.databinding.FragmentReservationsBinding
import com.tunombre.hotelreservas.ui.reservations.adapters.ReservaAdapter
import kotlinx.coroutines.launch

class ReservationsFragment : Fragment() {

    private var _binding: FragmentReservationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReservationsViewModel by viewModels()
    private lateinit var adapter: ReservaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        cargarReservas()
    }

    private fun setupRecyclerView() {
        adapter = ReservaAdapter()
        binding.recyclerViewReservas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ReservationsFragment.adapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.reservas.collect { reservas ->
                adapter.submitList(reservas)
                binding.textEmpty.visibility = if (reservas.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.loading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
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

    private fun cargarReservas() {
        val currentUser = FirebaseManager.auth.currentUser
        if (currentUser != null) {
            viewModel.cargarReservas(currentUser.uid)
        } else {
            binding.textEmpty.visibility = View.VISIBLE
            binding.textEmpty.text = "Inicia sesión para ver tus reservas"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}