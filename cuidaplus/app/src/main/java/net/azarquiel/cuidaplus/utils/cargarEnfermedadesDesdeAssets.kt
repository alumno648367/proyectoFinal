package net.azarquiel.cuidaplus.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.azarquiel.cuidaplus.model.Enfermedad

fun cargarEnfermedadesDesdeAssets(context: Context): List<Enfermedad> {
    val json = context.assets.open("enfermedades_cuidaplus.json").bufferedReader().use { it.readText() }
    val gson = Gson()
    val type = object : TypeToken<List<Enfermedad>>() {}.type
    return gson.fromJson(json, type)
}
