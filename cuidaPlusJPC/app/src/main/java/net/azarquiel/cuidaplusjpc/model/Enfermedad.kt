package net.azarquiel.cuidaplusjpc.model

data class Enfermedad(
    var enfermedadId: String = "",  // ID único de la enfermedad
    var nombre: String = "",        // Nombre de la enfermedad (ej: Alzheimer)
    var categoria: String = ""      // Categoría (ej: Enfermedades neurológicas)
)
