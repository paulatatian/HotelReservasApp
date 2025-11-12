package com.tunombre.hotelreservas.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tunombre.hotelreservas.data.FirebaseManager
import com.tunombre.hotelreservas.data.models.Usuario
import com.tunombre.hotelreservas.data.repositories.UsuarioRepository
import com.tunombre.hotelreservas.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val usuarioRepository = UsuarioRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    init {
        _currentUser.value = FirebaseManager.auth.currentUser
    }

    fun login(email: String, password: String) {
        if (!Validators.isValidEmail(email)) {
            _authState.value = AuthState.Error("Email no válido")
            return
        }

        if (!Validators.isValidPassword(password)) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        _authState.value = AuthState.Loading

        FirebaseManager.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = FirebaseManager.auth.currentUser
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error en el login")
                }
            }
    }

    fun register(email: String, password: String, confirmPassword: String, nombre: String) {
        if (!Validators.isValidEmail(email)) {
            _authState.value = AuthState.Error("Email no válido")
            return
        }

        if (!Validators.isValidPassword(password)) {
            _authState.value = AuthState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        if (!Validators.isValidName(nombre)) {
            _authState.value = AuthState.Error("Nombre no válido")
            return
        }

        _authState.value = AuthState.Loading

        FirebaseManager.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = FirebaseManager.auth.currentUser
                    if (firebaseUser != null) {
                        val usuario = Usuario(
                            id = firebaseUser.uid,
                            email = email,
                            nombre = nombre
                        )

                        viewModelScope.launch {
                            usuarioRepository.crearUsuario(usuario) { success ->
                                if (success) {
                                    _currentUser.value = firebaseUser
                                    _authState.value = AuthState.Success
                                } else {
                                    _authState.value = AuthState.Error("Error al crear perfil de usuario")
                                }
                            }
                        }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Error en el registro")
                }
            }
    }

    fun logout() {
        FirebaseManager.auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}