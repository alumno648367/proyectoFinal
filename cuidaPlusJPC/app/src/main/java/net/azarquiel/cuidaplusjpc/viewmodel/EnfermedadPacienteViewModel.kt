package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.EnfermedadPaciente
import net.azarquiel.cuidaplusjpc.repository.EnfermedadPacienteRepository

class EnfermedadPacienteViewModel : ViewModel() {

    private val repo = EnfermedadPacienteRepository()
    val relaciones = MutableLiveData<List<EnfermedadPaciente>>()

    fun cargarPorPaciente(pacienteId: String) {
        repo.enfermedadPorPaciente(pacienteId) { lista ->
            relaciones.value = lista
        }
    }

    fun guardarRelacion(
        ep: EnfermedadPaciente,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        repo.guardarEnfermedadPaciente(ep, onSuccess, onFailure)
    }

    fun eliminarRelacion(
        enfermedadPacienteId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        repo.eliminarEnfermedadPaciente(enfermedadPacienteId, onSuccess, onFailure)
    }
}
