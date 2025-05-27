package net.azarquiel.cuidaplusjpc.model

data class MedicamentoMaestro(
    var medicamentoId: String = "",
    var nombre: String = "",
    var tipo: String = "" // opcional: por ejemplo "Analgésico", "Antibiótico", etc.
)
