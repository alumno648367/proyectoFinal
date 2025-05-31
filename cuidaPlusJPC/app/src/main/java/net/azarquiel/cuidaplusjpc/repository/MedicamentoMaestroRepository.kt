package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.cuidaplusjpc.model.MedicamentoMaestro

class MedicamentoMaestroRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene la lista completa de medicamentos del maestro,
     * ordenados alfabéticamente por nombre.
     */
    fun getMedicamentos(
        onResult: (List<MedicamentoMaestro>) -> Unit
    ) {
        db.collection("medicamentos_maestro")
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(MedicamentoMaestro::class.java)
                onResult(lista)
            }
    }
}
