package net.azarquiel.cuidaplusjpc.repository

import Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UsuarioRepository {

    // Conexión principal a Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referencia directa a la colección "usuarios"
    private val ref = db.collection("usuarios")

    /**
     * Guarda el documento del usuario en la base de datos
     * - Si el documento ya existe, lo sobrescribe
     * - El ID del documento es el UID del usuario autenticado
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
     * Escucha los cambios en tiempo real del documento de un usuario
     * - Cada vez que el documento se actualiza, se recibe el nuevo objeto Usuario
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
     * Actualiza campos específicos del documento del usuario
     * - Solo modifica los campos incluidos en el Map
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
     * Elimina el documento completo del usuario en Firestore
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
}
