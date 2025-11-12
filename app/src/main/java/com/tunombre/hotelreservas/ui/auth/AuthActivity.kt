package com.tunombre.hotelreservas.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tunombre.hotelreservas.databinding.ActivityAuthBinding
import com.tunombre.hotelreservas.ui.MainActivity
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        android.widget.Toast.makeText(
                            this@AuthActivity,
                            state.message,
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val nombre = binding.etNombre.text.toString()
            viewModel.register(email, password, confirmPassword, nombre)
        }

        binding.switchAuthMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etConfirmPassword.visibility = android.view.View.VISIBLE
                binding.etNombre.visibility = android.view.View.VISIBLE
                binding.btnLogin.visibility = android.view.View.GONE
                binding.btnRegister.visibility = android.view.View.VISIBLE
            } else {
                binding.etConfirmPassword.visibility = android.view.View.GONE
                binding.etNombre.visibility = android.view.View.GONE
                binding.btnLogin.visibility = android.view.View.VISIBLE
                binding.btnRegister.visibility = android.view.View.GONE
            }
        }
    }
}