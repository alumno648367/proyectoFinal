package net.azarquiel.cuidaplus.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplus.model.TratamientoMaestro

class TratamientoMaestroRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getTratamientos(onResult: (List<TratamientoMaestro>) -> Unit) {
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
