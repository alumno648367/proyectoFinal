package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Tratamiento
import net.azarquiel.cuidaplusjpc.repository.TratamientoRepository

class TratamientoViewModel : ViewModel() {

    private val repo = TratamientoRepository()

    private val _tratamientos = MutableLiveData<List<Tratamiento>>()
    val tratamientos: LiveData<List<Tratamiento>> = _tratamientos

    val tratamientosPorEnfermedad = mutableStateMapOf<String, List<Tratamiento>>()

    fun cargarTratamientos(enfermedadPacienteId: String) {
        repo.getTratamientosPorEnfermedadPaciente(enfermedadPacienteId) { lista ->
            tratamientosPorEnfermedad[enfermedadPacienteId] = lista
        }
    }

    fun guardarTratamiento(tratamiento: Tratamiento) {
        repo.guardarTratamiento(tratamiento, {}, {})
    }

    fun eliminarTratamiento(tratamientoId: String) {
        repo.eliminarTratamiento(tratamientoId, {}, {})
    }
}
