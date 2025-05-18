package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar

class GrupoFamiliarRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("gruposFamiliares")

    // Crear grupo
    fun crearGrupo(
        grupo: GrupoFamiliar,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(grupo.grupoFamiliarId).set(grupo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Obtener grupo por ID
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

    // Añadir miembro al grupo
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


    // Editar grupo (nombre, pacientes...)
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

    // Eliminar grupo
    fun eliminarGrupo(
        grupoId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(grupoId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
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
