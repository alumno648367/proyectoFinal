package net.azarquiel.cuidaplusjpc.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.azarquiel.cuidaplusjpc.model.Medicamento
import net.azarquiel.cuidaplusjpc.repository.MedicamentoRepository

class MedicamentoViewModel : ViewModel() {

    private val repo = MedicamentoRepository()

    val medicamentosPorEnfermedad = mutableStateMapOf<String, List<Medicamento>>()

    val dosisDisponibles = listOf(
        "100 mg", "250 mg", "500 mg", "750 mg", "1 g",
        "5 ml", "10 ml", "15 ml",
        "1 pastilla", "2 pastillas",
        "1 comprimido", "2 comprimidos",
        "1 cápsula",
        "1 sobre", "2 sobres",
        "1 aplicación", "2 aplicaciones",
        "Aplicar cantidad fina"
    )

    val frecuenciasDisponibles = listOf(
        "Cada 4 horas", "Cada 6 horas", "Cada 8 horas",
        "Cada 12 horas", "Cada 24 horas",
        "Una vez al día", "Dos veces al día",
        "Antes de dormir", "Después de comer", "Al levantarse"
    )

    val viasAdministracion = listOf(
        "Oral", "Inyectable", "Intravenosa", "Intramuscular",
        "Tópica", "Sublingual", "Ocular", "Rectal",
        "Nasal", "Cutánea", "Pulmonar (inhalador)"
    )

    fun cargarMedicamentos(enfermedadPacienteId: String) {
        repo.getMedicamentosPorEnfermedad(enfermedadPacienteId) { lista ->
            medicamentosPorEnfermedad[enfermedadPacienteId] = lista
        }
    }

    fun guardarMedicamento(m: Medicamento) {
        repo.guardarMedicamento(m, {}, {})
    }

    fun eliminarMedicamento(medicamentoId: String) {
        repo.eliminarMedicamento(medicamentoId, {}, {})
    }
}

