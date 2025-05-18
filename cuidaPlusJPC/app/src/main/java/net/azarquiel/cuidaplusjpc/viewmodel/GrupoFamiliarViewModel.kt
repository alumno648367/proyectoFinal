package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar
import net.azarquiel.cuidaplusjpc.repository.GrupoFamiliarRepository

class GrupoFamiliarViewModel : ViewModel() {

    private val repo = GrupoFamiliarRepository()

    val grupo = MutableLiveData<GrupoFamiliar?>()

    // Crear grupo
    fun crearGrupo(
        grupoFamiliar: GrupoFamiliar,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.crearGrupo(grupoFamiliar, onSuccess, onFailure)
    }

    // Obtener grupo por ID
    fun cargarGrupo(grupoId: String) {
        repo.obtenerGrupo(grupoId) {
            grupo.value = it
        }
    }

    // Añadir miembro al grupo
    fun añadirMiembro(grupoId: String,uid: String,onSuccess: () -> Unit = {},onFailure: (Exception) -> Unit = {}
    ) {
        repo.añadirMiembro(grupoId, uid, onSuccess, onFailure)
    }


    // Editar grupo (ej: nombre o lista de pacientes)
    fun editarGrupo(grupoId: String, nuevosDatos: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.editarGrupo(grupoId, nuevosDatos, onSuccess, onFailure)
    }

    // Eliminar grupo por ID
    fun eliminarGrupo(grupoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        repo.eliminarGrupo(grupoId, onSuccess, onFailure)
    }
    fun escucharGrupo(grupoId: String) {
        repo.escucharGrupo(grupoId) {
            grupo.value = it
        }
    }

}
