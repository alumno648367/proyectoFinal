package net.azarquiel.cuidaplusjpc.model

data class Usuario(
    var usuarioId: String = "",         // UID del usuario (Firebase Auth)
    var nombre: String = "",
    var fechaNacimiento: String = "",   // Puedes usar Date si lo manejas con adaptadores
    var email: String = "",
    var numTelefono: String = "",
    var grupoFamiliarId: String = "",   // Referencia al grupo al que pertenece
)
