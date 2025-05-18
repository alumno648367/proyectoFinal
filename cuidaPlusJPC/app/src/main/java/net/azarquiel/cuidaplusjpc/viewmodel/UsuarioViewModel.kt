package net.azarquiel.cuidaplusjpc.viewmodel

import Usuario
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    fun actualizarUsuario(uid: String, campos: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.actualizarUsuario(uid, campos, onSuccess, onFailure)
    }

    fun eliminarUsuario(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.eliminarUsuario(uid, onSuccess, onFailure)
    }

}
