package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Enfermedad
import net.azarquiel.cuidaplusjpc.repository.EnfermedadRepository

class EnfermedadViewModel : ViewModel() {
    private val repo = EnfermedadRepository()

    private val _enfermedades = MutableLiveData<List<Enfermedad>>()
    val enfermedades: MutableLiveData<List<Enfermedad>> = _enfermedades

    fun cargarEnfermedades() {
        repo.obtenerEnfermedades { _enfermedades.value = it }
    }
}
