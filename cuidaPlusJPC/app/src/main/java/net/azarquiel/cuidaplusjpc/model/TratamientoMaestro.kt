package net.azarquiel.cuidaplusjpc.model

data class TratamientoMaestro(
    var tratamientoId: String = "",    // ID único del tratamiento en el catálogo
    var nombre: String = "",           // Nombre del tratamiento (ej: Rehabilitación rodilla)
    var tipo: String = ""              // Tipo (ej: Farmacológico, Fisioterapia, etc.)
)
