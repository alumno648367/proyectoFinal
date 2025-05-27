package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.Medicamento

class MedicamentoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("medicamentos")

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

    fun getMedicamentosPorTratamiento(
        tratamientoId: String,
        onResult: (List<Medicamento>) -> Unit
    ) {
        ref.whereEqualTo("tratamientoId", tratamientoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Medicamento::class.java)
                onResult(lista)
            }
    }

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
