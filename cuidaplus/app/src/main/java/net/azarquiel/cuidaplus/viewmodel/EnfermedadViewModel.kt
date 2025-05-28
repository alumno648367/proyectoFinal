package net.azarquiel.cuidaplus.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplus.model.Enfermedad
import net.azarquiel.cuidaplus.repository.EnfermedadRepository

class EnfermedadViewModel : ViewModel() {
    private val repo = EnfermedadRepository()

    private val _enfermedades = MutableLiveData<List<Enfermedad>>()
    val enfermedades: MutableLiveData<List<Enfermedad>> = _enfermedades

    fun cargarEnfermedades() {
        repo.obtenerEnfermedades { lista ->
            Log.d("ENFERMEDADES", "Cargadas: ${lista.size}")

            enfermedades.value = lista
        }
    }



}
