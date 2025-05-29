package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.azarquiel.cuidaplusjpc.model.CitaMedica
import net.azarquiel.cuidaplusjpc.repository.CitaMedicaRepository

class CitaMedicaViewModel : ViewModel() {

    private val repository = CitaMedicaRepository()

    private val _citas = MutableStateFlow<List<CitaMedica>>(emptyList())
    val citas: StateFlow<List<CitaMedica>> = _citas

    fun cargarCitasDePaciente(pacienteId: String) {
        repository.getCitasPorPaciente(pacienteId) { lista ->
            _citas.value = lista
        }
    }

    fun guardarCita(
        cita: CitaMedica,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.guardarCita(cita, onSuccess, onFailure)
    }

    fun eliminarCita(
        citaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repository.eliminarCita(citaId, onSuccess, onFailure)
    }
    fun cargarCitasDePacientes(listaPacientesId: List<String>) {
        repository.getCitasPorPacientes(listaPacientesId) { lista ->
            _citas.value = lista
        }
    }

}
