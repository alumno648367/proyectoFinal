package net.azarquiel.cuidaplusjpc.model

data class EnfermedadPaciente(
    var enfermedadPacienteId: String = "",  // ID único de la enfermedad del paciente
    var pacienteId: String = "",            // ID del paciente asociado
    var enfermedadId: String = "",          // ID de la enfermedad (del catálogo maestro)
    var nombre: String = "",                // Nombre de la enfermedad (copiado del catálogo)
    var categoria: String = "",             // Categoría de la enfermedad (copiada del catálogo)
    var fechaDiagnostico: String = "",      // Fecha en la que se diagnosticó
    var estado: String = "",                // Estado actual (ej: activo, controlado)
    var observaciones: String = ""          // Notas adicionales
)
