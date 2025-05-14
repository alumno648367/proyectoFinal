package net.azarquiel.cuidaplusjpc.model

data class ArchivoAdjunto(
    var archivoId: String = "",              // ID único
    var pacienteId: String = "",             // Relación con paciente
    var nombreArchivo: String = "",          // Ej: "informe_renal.pdf"
    var url: String = "",                    // URL de Firebase Storage
    var tipo: String = "",                   // Ej: "pdf", "imagen"
    var fecha: String = "",                  // Fecha de subida
    var subidoPorUsuarioId: String = ""      // Quién lo subió
)
