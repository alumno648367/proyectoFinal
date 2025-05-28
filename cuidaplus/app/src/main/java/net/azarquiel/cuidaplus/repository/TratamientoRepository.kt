package net.azarquiel.cuidaplus.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplus.model.Tratamiento

class TratamientoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("tratamientos")

    fun guardarTratamiento(
        tratamiento: Tratamiento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(tratamiento.tratamientoId)
            .set(tratamiento)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getTratamientosPorEnfermedadPaciente(
        enfermedadPacienteId: String,
        onResult: (List<Tratamiento>) -> Unit
    ) {
        ref.whereEqualTo("enfermedadPacienteId", enfermedadPacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Tratamiento::class.java)
                onResult(lista)
            }
    }

    fun eliminarTratamiento(
        tratamientoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(tratamientoId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
