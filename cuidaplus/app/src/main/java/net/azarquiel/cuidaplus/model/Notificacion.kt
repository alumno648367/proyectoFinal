package net.azarquiel.cuidaplus.model

data class Notificacion(
    var notificacionId: String = "",               // ID único
    var usuarioId: String = "",                    // Usuario que recibe la notificación
    var tipo: String = "",                         // Ej: "cita", "medicamento"
    var mensaje: String = "",                      // Texto a mostrar
    var fecha: String = "",                        // Fecha/hora de la notificación
    var estado: String = "",                       // "pendiente", "vista", etc.
    var referenciaCitaId: String? = null,          // Opcional, si aplica
    var referenciaMedicamentoId: String? = null    // Opcional, si aplica
)
