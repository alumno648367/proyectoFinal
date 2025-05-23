package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.azarquiel.cuidaplusjpc.model.GrupoFamiliar

class GrupoFamiliarRepository {

    // Referencia a la instancia de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referencia a la colección "gruposFamiliares"
    private val ref = db.collection("gruposFamiliares")

    /**
     * Crea un nuevo documento en la colección "gruposFamiliares"
     * con los datos del grupo proporcionado
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
     * Obtiene un grupo por su ID y lo devuelve como objeto
     * a través del callback 'onResult'
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
     * Añade un UID a la lista de miembros del grupo
     * usando FieldValue.arrayUnion para evitar duplicados
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
     * Actualiza campos específicos del grupo familiar
     * (por ejemplo nombre o lista de pacientes)
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
     * Elimina completamente el documento del grupo de la colección
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
     * Escucha en tiempo real los cambios del grupo por su ID
     * Ideal para actualizar la interfaz automáticamente si se modifica
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
    fun obtenerGrupoPorNombre(nombre: String, onResult: (GrupoFamiliar?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
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

}
