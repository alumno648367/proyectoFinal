package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.CitaMedica
import net.azarquiel.cuidaplusjpc.repository.CitaMedicaRepository

class CitaViewModel : ViewModel() {

    private val repo = CitaMedicaRepository()

    // Lista para el Listado
    val citas: SnapshotStateList<CitaMedica> = mutableStateListOf()

    // Cita que estamos editando o visualizando
    val citaActual = mutableStateOf<CitaMedica?>(null)

    fun cargarCitaPorId(citaId: String) {
        repo.getCitaPorId(citaId) { cita ->
            citaActual.value = cita
        }
    }

    fun cargarCitasPorGrupo(grupoFamiliarId: String) {
        repo.getCitasPorGrupo(grupoFamiliarId) { lista ->
            citas.clear()
            citas.addAll(lista)
        }
    }

    fun guardarCita(
        cita: CitaMedica,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.guardarCita(cita, onSuccess, onFailure)
    }

    fun eliminarCita(
        citaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.eliminarCita(citaId, onSuccess, onFailure)
    }

    fun actualizarRealizada(
        citaId: String,
        realizada: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        repo.actualizarEstadoCita(citaId, realizada, onSuccess, onFailure)
    }
}
