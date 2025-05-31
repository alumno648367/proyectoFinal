package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar

class GrupoFamiliarRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("gruposFamiliares")

    /**
     * Crea un grupo familiar con el ID y datos proporcionados.
     */
    fun crearGrupo(
        grupo: GrupoFamiliar,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(grupo.grupoFamiliarId).set(grupo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Obtiene un grupo familiar por su ID.
     */
    fun obtenerGrupo(
        grupoId: String,
        onResult: (GrupoFamiliar?) -> Unit
    ) {
        ref.document(grupoId).get()
            .addOnSuccessListener { doc ->
                val grupo = doc.toObject<GrupoFamiliar>()
                onResult(grupo)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Obtiene un grupo familiar por su nombre (consulta única).
     */
    fun obtenerGrupoPorNombre(
        nombre: String,
        onResult: (GrupoFamiliar?) -> Unit
    ) {
        db.collection("gruposFamiliares")
            .whereEqualTo("nombre", nombre)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    onResult(null)
                } else {
                    val grupo = result.documents.firstOrNull()?.toObject(GrupoFamiliar::class.java)
                    onResult(grupo)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Añade un nuevo miembro (UID) al grupo usando arrayUnion.
     */
    fun añadirMiembro(
        grupoId: String,
        uid: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        ref.document(grupoId)
            .update("miembros", FieldValue.arrayUnion(uid))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Edita campos específicos del grupo familiar.
     */
    fun editarGrupo(
        grupoId: String,
        nuevosDatos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(grupoId).update(nuevosDatos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Elimina por completo el grupo familiar.
     */
    fun eliminarGrupo(
        grupoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(grupoId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Escucha en tiempo real los cambios en el grupo familiar.
     */
    fun escucharGrupo(
        grupoId: String,
        onChange: (GrupoFamiliar?) -> Unit
    ) {
        ref.document(grupoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onChange(null)
                    return@addSnapshotListener
                }
                val grupo = snapshot?.toObject<GrupoFamiliar>()
                onChange(grupo)
            }
    }
}
