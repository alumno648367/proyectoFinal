package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.EnfermedadPaciente
import net.azarquiel.cuidaplusjpc.model.Paciente
import net.azarquiel.cuidaplusjpc.repository.EnfermedadPacienteRepository

class EnfermedadPacienteViewModel : ViewModel() {

    private val repo = EnfermedadPacienteRepository()
    val relaciones = MutableLiveData<List<EnfermedadPaciente>>()

    val enfermedadesPorPaciente = MutableLiveData<Map<String, List<EnfermedadPaciente>>>(emptyMap())

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

    fun cargarEnfermedadesParaPacientes(pacientes: List<Paciente>) {
        val repo = EnfermedadPacienteRepository()
        val mapa = mutableMapOf<String, List<EnfermedadPaciente>>()

        pacientes.forEach { paciente ->
            repo.enfermedadPorPaciente(paciente.pacienteId) { lista ->
                mapa[paciente.pacienteId] = lista
                enfermedadesPorPaciente.postValue(mapa.toMap())
            }
        }
    }
    fun clearEnfermedades() {
        relaciones.value = emptyList()
        enfermedadesPorPaciente.value = emptyMap()
    }




}