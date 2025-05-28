package net.azarquiel.cuidaplus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplus.model.MedicamentoMaestro
import net.azarquiel.cuidaplus.repository.MedicamentoMaestroRepository

class MedicamentoMaestroViewModel : ViewModel() {

    private val repo = MedicamentoMaestroRepository()

    private val _medicamentos = MutableLiveData<List<MedicamentoMaestro>>()
    val medicamentos: LiveData<List<MedicamentoMaestro>> = _medicamentos

    fun cargarMedicamentos() {
        repo.getMedicamentos { lista ->
            _medicamentos.value = lista
        }
    }
}
