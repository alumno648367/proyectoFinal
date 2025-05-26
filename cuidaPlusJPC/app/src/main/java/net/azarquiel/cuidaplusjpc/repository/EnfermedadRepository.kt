package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.Enfermedad

class EnfermedadRepository {
    private val db = FirebaseFirestore.getInstance()

    fun obtenerEnfermedades(onResult: (List<Enfermedad>) -> Unit) {
        db.collection("enfermedades")
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Enfermedad::class.java)
                onResult(lista)
            }
    }

}
