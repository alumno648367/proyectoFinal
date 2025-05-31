package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.Medicamento

class MedicamentoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("medicamentos")

    /**
     * Guarda o actualiza un medicamento en Firestore.
     */
    fun guardarMedicamento(
        medicamento: Medicamento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(medicamento.medicamentoId)
            .set(medicamento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Recupera los medicamentos asociados a una enfermedad de un paciente.
     */
    fun getMedicamentosPorEnfermedadPaciente(
        enfermedadPacienteId: String,
        onResult: (List<Medicamento>) -> Unit
    ) {
        ref.whereEqualTo("enfermedadPacienteId", enfermedadPacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Medicamento::class.java)
                onResult(lista)
            }
    }

    /**
     * Elimina un medicamento por su ID.
     */
    fun eliminarMedicamento(
        medicamentoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(medicamentoId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
