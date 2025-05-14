package net.azarquiel.cuidaplusjpc.model

data class Medicamento(
    var medicamentoId: String = "",                  // ID único
    var tratamientoId: String = "",                  // Relación con el tratamiento
    var nombre: String = "",
    var dosis: String = "",                          // Ej: "500 mg"
    var frecuencia: String = "",                     // Ej: "Cada 8 horas"
    var viaAdministracion: String = "",              // Ej: "Oral", "Intravenosa"
    var horario: List<String> = emptyList(),         // Ej: ["08:00", "16:00", "00:00"]
    var observaciones: String = ""
)
