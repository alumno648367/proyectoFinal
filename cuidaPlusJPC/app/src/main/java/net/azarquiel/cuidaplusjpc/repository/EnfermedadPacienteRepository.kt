package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.EnfermedadPaciente

class EnfermedadPacienteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("enfermedadesPaciente")

    /**
     * Recupera todas las enfermedades asociadas a un paciente.
     */
    fun enfermedadPorPaciente(
        pacienteId: String,
        onResult: (List<EnfermedadPaciente>) -> Unit
    ) {
        ref.whereEqualTo("pacienteId", pacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot.toObjects(EnfermedadPaciente::class.java)
                onResult(lista)
            }
    }

    /**
     * Guarda o actualiza una enfermedad asignada a un paciente.
     */
    fun guardarEnfermedadPaciente(
        ep: EnfermedadPaciente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(ep.enfermedadPacienteId)
            .set(ep)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Elimina una enfermedad asociada a un paciente por su ID.
     */
    fun eliminarEnfermedadPaciente(
        enfermedadPacienteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(enfermedadPacienteId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
