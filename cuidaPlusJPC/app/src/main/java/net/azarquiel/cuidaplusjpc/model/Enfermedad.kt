package net.azarquiel.cuidaplusjpc.model

data class Enfermedad(
    var enfermedadId: String = "",   // ID único
    var nombre: String = "",
    var tipo: String = "",           // Ej: "Crónica", "Infecciosa", etc.
    var descripcion: String = ""
)
