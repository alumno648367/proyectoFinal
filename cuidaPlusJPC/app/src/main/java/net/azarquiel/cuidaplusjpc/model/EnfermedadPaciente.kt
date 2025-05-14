package net.azarquiel.cuidaplusjpc.model

data class EnfermedadPaciente(
    var enfermedadPacienteId: String = "",      // ID único
    var pacienteId: String = "",                // Relación con el paciente
    var enfermedadId: String = "",              // Relación con Enfermedad (nombre, tipo...)
    var fechaDiagnostico: String = "",          // O tipo Date/Timestamp
    var estado: String = "",                    // Ej: "Activa", "En remisión", etc.
    var observaciones: String = "",
    var tratamientos: List<String> = emptyList()  // IDs de tratamientos o subcolección
)
