package net.azarquiel.cuidaplusjpc.model

data class MedicamentoMaestro(
    var medicamentoId: String = "",       // ID único del medicamento en el catálogo
    var nombre: String = "",              // Nombre genérico o comercial
    var tipo: String = ""                 // Tipo opcional (ej: Analgésico, Antibiótico, etc.)
)
