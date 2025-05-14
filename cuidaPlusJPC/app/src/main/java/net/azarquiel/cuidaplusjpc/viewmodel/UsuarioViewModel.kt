package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Usuario
import net.azarquiel.cuidaplusjpc.repository.UsuarioRepository

class UsuarioViewModel : ViewModel() {

    private val repo = UsuarioRepository()


    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: MutableLiveData<Usuario?> = _usuario

    // Escucha en tiempo real al usuario por su ID
    fun empezarEscucha(uid: String) {
        repo.escucharUsuario(uid) { _usuario.value = it }
    }

    // Guarda el usuario completo en Firestore
    fun guardarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.guardarUsuario(usuario, onSuccess, onFailure)
    }
}
