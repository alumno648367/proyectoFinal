package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.TratamientoMaestro

class TratamientoMaestroRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene todos los tratamientos maestros ordenados alfabéticamente por nombre.
     */
    fun getTratamientos(
        onResult: (List<TratamientoMaestro>) -> Unit
    ) {
        db.collection("tratamientos_maestro")
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot.toObjects(TratamientoMaestro::class.java)
                onResult(lista)
            }
    }
}
