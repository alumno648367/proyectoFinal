package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar
import net.azarquiel.cuidaplusjpc.repository.GrupoFamiliarRepository

class GrupoFamiliarViewModel : ViewModel() {

    // Repositorio que maneja la conexión con Firestore para los grupos familiares
    private val repo = GrupoFamiliarRepository()

    // LiveData con el grupo actualmente cargado o escuchado
    val grupo = MutableLiveData<GrupoFamiliar?>()

    /**
     * Crea un nuevo grupo familiar en Firestore
     * Se usa al registrar al primer usuario del grupo
     */
    fun crearGrupo(
        grupoFamiliar: GrupoFamiliar,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.crearGrupo(grupoFamiliar, onSuccess, onFailure)
    }

    /**
     * Carga un grupo por su ID y actualiza el LiveData
     * Se usa para obtener los datos del grupo cuando el usuario ya pertenece
     */
    fun cargarGrupo(grupoId: String) {
        repo.obtenerGrupo(grupoId) {
            grupo.value = it
        }
    }

    /**
     * Añade un miembro al grupo (por su UID)
     * Se usa al registrarse en un grupo existente
     */
    fun añadirMiembro(
        grupoId: String,
        uid: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        repo.añadirMiembro(grupoId, uid, onSuccess, onFailure)
    }

    /**
     * Edita campos específicos del grupo (nombre, pacientes, etc.)
     */
    fun editarGrupo(
        grupoId: String,
        nuevosDatos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.editarGrupo(grupoId, nuevosDatos, onSuccess, onFailure)
    }

    /**
     * Elimina un grupo familiar completamente de Firestore
     */
    fun eliminarGrupo(
        grupoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.eliminarGrupo(grupoId, onSuccess, onFailure)
    }

    /**
     * Escucha en tiempo real los cambios en un grupo
     * Se usa si quieres que los miembros o datos se actualicen automáticamente
     */
    fun escucharGrupo(grupoId: String) {
        repo.escucharGrupo(grupoId) {
            grupo.value = it
        }
    }
    fun obtenerGrupoPorNombre(nombre: String, onResult: (GrupoFamiliar?) -> Unit) {
        repo.obtenerGrupoPorNombre(nombre, onResult)
    }




}
