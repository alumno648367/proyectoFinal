package net.azarquiel.cuidaplusjpc.repository

import Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.FieldPath

class UsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("usuarios")

    /**
     * Guarda o actualiza el documento del usuario en Firestore.
     * El ID usado es el UID del usuario autenticado.
     */
    fun guardarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(usuario.usuarioId)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Escucha en tiempo real los cambios de un usuario por su UID.
     */
    fun escucharUsuario(
        uid: String,
        onChange: (Usuario?) -> Unit
    ) {
        ref.document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val usuario = snapshot?.toObject<Usuario>()
                onChange(usuario)
            }
    }

    /**
     * Actualiza campos específicos del usuario por su UID.
     */
    fun actualizarUsuario(
        uid: String,
        campos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(uid).update(campos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Elimina completamente el documento del usuario en Firestore.
     */
    fun eliminarUsuario(
        uid: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(uid).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Obtiene una lista de usuarios a partir de sus UIDs (máx. 10 por restricción de Firebase).
     */
    fun getUsuariosPorIds(
        ids: List<String>,
        onResult: (List<Usuario>) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }

        ref.whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.mapNotNull { it.toObject<Usuario>() }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
    fun escucharUsuariosPorIds(
        ids: List<String>,
        onResult: (List<Usuario>) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }

        val usuarios = mutableMapOf<String, Usuario>()

        ids.forEach { id ->
            ref.document(id).addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val usuario = snapshot.toObject(Usuario::class.java)
                    if (usuario != null) {
                        usuarios[id] = usuario
                        onResult(usuarios.values.toList())
                    }
                }
            }
        }
    }
}
