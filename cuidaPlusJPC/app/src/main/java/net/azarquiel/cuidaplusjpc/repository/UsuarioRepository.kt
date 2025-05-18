package net.azarquiel.cuidaplusjpc.repository

import Usuario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class UsuarioRepository {

    // Conexión a la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referencia a la colección "usuarios"
    private val ref = db.collection("usuarios")

    // Guarda el usuario completo en Firestore
    fun guardarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // crea o sobreescribe el documento con el ID del usuario
        ref.document(usuario.usuarioId)
            .set(usuario)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Escucha en tiempo real los cambios del usuario por su ID
    fun escucharUsuario(
        uid: String,
        onChange: (Usuario?) -> Unit
    ) {
        ref.document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val usuario = snapshot?.toObject<Usuario>() // convierte documento a objeto Usuario
                onChange(usuario) // pasa el resultado al ViewModel
            }
    }

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
