package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.Tratamiento

class TratamientoRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("tratamientos")

    /**
     * Guarda o actualiza un tratamiento en Firestore.
     */
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

    /**
     * Obtiene los tratamientos asociados a una enfermedad concreta de un paciente.
     */
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

    /**
     * Elimina un tratamiento por su ID.
     */
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
