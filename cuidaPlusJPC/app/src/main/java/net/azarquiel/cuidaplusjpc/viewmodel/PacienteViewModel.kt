package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.repository.PacienteRepository

class PacienteViewModel : ViewModel() {

    private val repo = PacienteRepository()

    val pacientes = MutableLiveData<List<Paciente>>()

    fun cargarPacientesDelGrupo(grupoId: String) {
        repo.obtenerPacientesPorGrupo(grupoId) {
            pacientes.value = it
        }
    }

    fun guardarPaciente(
        paciente: Paciente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.guardarPaciente(paciente, onSuccess, onFailure)
    }

    fun actualizarPaciente(
        pacienteId: String,
        campos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.actualizarPaciente(pacienteId, campos, onSuccess, onFailure)
    }

    fun eliminarPaciente(
        pacienteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.eliminarPaciente(pacienteId, onSuccess, onFailure)
    }
}
