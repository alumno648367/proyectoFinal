package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.TratamientoMaestro
import net.azarquiel.cuidaplusjpc.repository.TratamientoMaestroRepository

class TratamientoMaestroViewModel : ViewModel() {

    private val repo = TratamientoMaestroRepository()

    private val _tratamientos = MutableLiveData<List<TratamientoMaestro>>()
    val tratamientos: LiveData<List<TratamientoMaestro>> = _tratamientos

    fun cargarTratamientos() {
        repo.getTratamientos { lista ->
            _tratamientos.value = lista
        }
    }
    fun clearTratamientos() {
        _tratamientos.value = emptyList()
    }
}
