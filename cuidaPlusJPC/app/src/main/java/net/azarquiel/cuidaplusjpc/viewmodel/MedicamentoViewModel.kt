package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Medicamento
import net.azarquiel.cuidaplusjpc.repository.MedicamentoRepository

class MedicamentoViewModel : ViewModel() {

    private val repo = MedicamentoRepository()

    private val _medicamentos = MutableLiveData<List<Medicamento>>()
    val medicamentos: LiveData<List<Medicamento>> = _medicamentos

    fun cargarMedicamentos(tratamientoId: String) {
        repo.getMedicamentosPorTratamiento(tratamientoId) { lista ->
            _medicamentos.value = lista
        }
    }

    fun guardarMedicamento(medicamento: Medicamento) {
        repo.guardarMedicamento(medicamento, {}, {})
    }

    fun eliminarMedicamento(medicamentoId: String) {
        repo.eliminarMedicamento(medicamentoId, {}, {})
    }
}
