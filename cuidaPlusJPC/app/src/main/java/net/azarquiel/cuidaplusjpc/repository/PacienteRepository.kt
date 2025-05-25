package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.azarquiel.cuidaplusjpc.model.Paciente

class PacienteRepository {

    // Conexión principal a Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    // Referencia directa a la colección "pacientes"
    private val ref = db.collection("pacientes")

    /**
     * Guarda el documento del paciente en la base de datos
     * - Si el documento ya existe, lo sobrescribe
     * - El ID del documento es el pacienteId único
     */
    fun guardarPaciente(
        paciente: Paciente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (paciente.pacienteId.isBlank()) {
            onFailure(IllegalArgumentException("El ID del paciente no puede estar vacío"))
            return
        }

        ref.document(paciente.pacienteId)
            .set(paciente)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Escucha los cambios en tiempo real de un paciente específico por ID
     * - Cada vez que el documento se actualiza, se recibe el nuevo objeto Paciente
     */
    fun escucharPaciente(
        pacienteId: String,
        onChange: (Paciente?) -> Unit
    ) {
        ref.document(pacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val paciente = snapshot?.toObject<Paciente>()
                onChange(paciente)
            }
    }

    /**
     * Obtiene la lista de pacientes que pertenecen a un grupo familiar
     * - Se filtra por grupoFamiliarId
     */
    fun obtenerPacientesPorGrupo(grupoId: String, onResult: (List<Paciente>) -> Unit) {
        db.collection("pacientes")
            .whereEqualTo("grupoFamiliarId", grupoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                val lista = snapshot.toObjects(Paciente::class.java)
                onResult(lista)
            }
    }


    /**
     * Actualiza campos específicos del documento del paciente
     * - Solo modifica los campos incluidos en el Map
     */
    fun actualizarPaciente(
        pacienteId: String,
        campos: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(pacienteId).update(campos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Elimina el documento completo del paciente en Firestore
     */
    fun eliminarPaciente(
        pacienteId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        ref.document(pacienteId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
