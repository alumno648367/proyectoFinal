package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.Medicamento

class MedicamentoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("medicamentos")

    fun guardarMedicamento(m: Medicamento, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ref.document(m.medicamentoId).set(m)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getMedicamentosPorEnfermedad(enfermedadPacienteId: String, onResult: (List<Medicamento>) -> Unit) {
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

    fun eliminarMedicamento(medicamentoId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        ref.document(medicamentoId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
