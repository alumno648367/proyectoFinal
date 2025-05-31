package net.azarquiel.cuidaplusjpc.viewmodel

import Usuario
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.repository.UsuarioRepository

class UsuarioViewModel : ViewModel() {

    // Repositorio encargado de acceder a Firebase Firestore
    private val repo = UsuarioRepository()

    // LiveData que contiene el usuario actual observado en tiempo real
    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: MutableLiveData<Usuario?> = _usuario

    val usuariosGrupo: MutableLiveData<List<Usuario>> = MutableLiveData()

    /**
     * Escucha los cambios en tiempo real del usuario según su UID
     * Actualiza automáticamente el LiveData cuando se detectan cambios en Firestore
     */
    fun empezarEscucha(uid: String) {
        repo.escucharUsuario(uid) { _usuario.value = it }
    }

    /**
     * Guarda un usuario completo en Firestore
     * Se llama después del registro completo (datos personales + grupo)
     */
    fun guardarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.guardarUsuario(usuario, onSuccess, onFailure)
    }

    /**
     * Actualiza campos específicos del usuario
     * Recibe un mapa con los campos a modificar
     */
    fun actualizarUsuario(
        uid: String,
        campos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.actualizarUsuario(uid, campos, onSuccess, onFailure)
    }

    /**
     * Elimina completamente el documento del usuario en Firestore
     */
    fun eliminarUsuario(
        uid: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.eliminarUsuario(uid, onSuccess, onFailure)
    }
    fun obtenerUsuariosPorIds(ids: List<String>) {
        repo.getUsuariosPorIds(ids) { lista ->
            usuariosGrupo.value = lista
        }
    }
    fun clearUsuario() {
        _usuario.value = null
        usuariosGrupo.value = emptyList()
    }
    fun clearUsuariosGrupo() {
        usuariosGrupo.value = emptyList()
    }



}
