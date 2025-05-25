package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.repository.PacienteRepository

class PacienteViewModel : ViewModel() {

    // Repositorio encargado de acceder a Firebase Firestore
    private val repo = PacienteRepository()

    // LiveData con la lista de pacientes del grupo
    private val _pacientes = MutableLiveData<List<Paciente>>()
    val pacientes: MutableLiveData<List<Paciente>> = _pacientes

    /**
     * Carga la lista de pacientes de un grupo específico
     */
    fun cargarPacientesDelGrupo(grupoId: String) {
        repo.obtenerPacientesPorGrupo(grupoId) { lista ->
            _pacientes.value = lista
        }
    }
    /**
     * Guarda un nuevo paciente en Firestore
     */
    fun guardarPaciente(
        paciente: Paciente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.guardarPaciente(paciente, onSuccess, onFailure)
    }

    fun escucharPaciente(
        pacienteId: String,
        onChange: (Paciente?) -> Unit
    ) {
        repo.escucharPaciente(pacienteId, onChange)
    }

    /**
     * Actualiza campos específicos de un paciente
     */
    fun actualizarPaciente(
        pacienteId: String,
        campos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.actualizarPaciente(pacienteId, campos, onSuccess, onFailure)
    }

    /**
     * Elimina completamente un paciente de Firestore
     */
    fun eliminarPaciente(
        pacienteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.eliminarPaciente(pacienteId, onSuccess, onFailure)
    }
}
