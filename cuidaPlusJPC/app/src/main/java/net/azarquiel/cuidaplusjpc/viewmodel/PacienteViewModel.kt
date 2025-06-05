package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.repository.EnfermedadPacienteRepository
import net.azarquiel.cuidaplusjpc.repository.PacienteRepository

class PacienteViewModel : ViewModel() {

    // Repositorio encargado de acceder a Firebase Firestore
    private val repo = PacienteRepository()
    private val repoEnfermedadPaciente = EnfermedadPacienteRepository()

    // LiveData con la lista de pacientes del grupo
    private val _pacientes = MutableLiveData<List<Paciente>>()
    val pacientes: MutableLiveData<List<Paciente>> = _pacientes

    val pacientesDelGrupo = MutableLiveData<List<Paciente>>()
    /**
     * Carga la lista de pacientes de un grupo específico
     */
    fun cargarPacientesDelGrupo(grupoId: String) {
        repo.obtenerPacientesPorGrupo(grupoId) { lista ->
            pacientesDelGrupo.value = lista  // ← CORRECTO
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

    /**
     * Actualiza el campo enfermedades con los IDs actuales del paciente
     */
    fun actualizarEnfermedadesDelPaciente(pacienteId: String) {
        repoEnfermedadPaciente.enfermedadPorPaciente(pacienteId) { relaciones ->
            val listaIds = relaciones.map { it.enfermedadId }
            actualizarPaciente(
                pacienteId,
                mapOf("enfermedades" to listaIds),
                onSuccess = {},
                onFailure = {}
            )
        }
    }

    fun escucharPacientesDelGrupo(grupoFamiliarId: String) {
        repo.getPacientesDelGrupo(grupoFamiliarId) { lista ->
            pacientesDelGrupo.value = lista
        }
    }
    fun clearPacientes() {
        pacientesDelGrupo.value = emptyList()
    }

}
