package net.azarquiel.cuidaplusjpc.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import net.azarquiel.cuidaplusjpc.model.Paciente

class PacienteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ref = db.collection("pacientes")

    /**
     * Guarda o actualiza un paciente en Firestore.
     * Si el ID está vacío, devuelve error.
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
     * Escucha en tiempo real los cambios de un paciente por ID.
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
     * Obtiene todos los pacientes de un grupo familiar por ID.
     */
    fun obtenerPacientesPorGrupo(
        grupoId: String,
        onResult: (List<Paciente>) -> Unit
    ) {
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
     * (Duplicado funcional) También obtiene pacientes de un grupo.
     * Se recomienda unificar en uno solo si no hay diferencias.
     */
    fun getPacientesDelGrupo(
        grupoFamiliarId: String,
        onResult: (List<Paciente>) -> Unit
    ) {
        FirebaseFirestore.getInstance()
            .collection("pacientes")
            .whereEqualTo("grupoFamiliarId", grupoFamiliarId)
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
     * Actualiza campos específicos del paciente por su ID.
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
     * Elimina el documento del paciente por su ID.
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
