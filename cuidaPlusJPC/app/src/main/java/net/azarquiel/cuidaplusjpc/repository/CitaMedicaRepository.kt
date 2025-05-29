package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import net.azarquiel.cuidaplusjpc.model.CitaMedica

class CitaMedicaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("citas_medicas")

    fun guardarCita(
        cita: CitaMedica,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val doc = if (cita.citaMedicaId.isEmpty()) {
            ref.document().also { cita.citaMedicaId = it.id }
        } else {
            ref.document(cita.citaMedicaId)
        }

        doc.set(cita)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCitasPorPaciente(
        pacienteId: String,
        onResult: (List<CitaMedica>) -> Unit
    ) {
        ref.whereEqualTo("pacienteId", pacienteId)
            .orderBy("fechaHora", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(CitaMedica::class.java)
                onResult(lista)
            }
    }

    fun eliminarCita(
        citaId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(citaId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun getCitasPorPacientes(
        listaPacientesId: List<String>,
        onResult: (List<CitaMedica>) -> Unit
    ) {
        if (listaPacientesId.isEmpty()) {
            onResult(emptyList())
            return
        }

        ref.whereIn("pacienteId", listaPacientesId.take(10)) // Firebase solo permite 10 elementos en whereIn
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot.toObjects(CitaMedica::class.java)
                onResult(lista)
            }
    }

}
