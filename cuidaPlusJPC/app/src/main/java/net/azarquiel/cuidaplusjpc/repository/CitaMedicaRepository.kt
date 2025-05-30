package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.DocumentSnapshot
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
    fun getCitaPorId(
        citaId: String,
        onResult: (CitaMedica?) -> Unit
    ) {
        ref.document(citaId)
            .addSnapshotListener { snap: DocumentSnapshot?, error ->
                if (error != null || snap == null || !snap.exists()) {
                    onResult(null)
                } else {
                    onResult(snap.toObject(CitaMedica::class.java))
                }
            }
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

    fun getCitasPorGrupo(
        grupoFamiliarId: String,
        onResult: (List<CitaMedica>) -> Unit
    ) {
        ref.whereEqualTo("grupoFamiliarId", grupoFamiliarId)
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

    fun actualizarEstadoCita(
        citaId: String,
        realizada: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(citaId)
            .update("realizada", realizada)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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
            ref.whereIn("pacienteId", listaPacientesId.take(10)) // límite Firebase
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
