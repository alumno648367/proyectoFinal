package net.azarquiel.cuidaplus.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.azarquiel.cuidaplus.model.MedicamentoMaestro
import net.azarquiel.cuidaplus.model.TratamientoMaestro

fun cargarMedicamentosMaestroDesdeAssets(context: Context): List<MedicamentoMaestro> {
    val json = context.assets.open("medicamentos_maestro.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<MedicamentoMaestro>>() {}.type
    return Gson().fromJson(json, type)
}

fun cargarTratamientosMaestroDesdeAssets(context: Context): List<TratamientoMaestro> {
    val json = context.assets.open("tratamientos_maestro.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<TratamientoMaestro>>() {}.type
    return Gson().fromJson(json, type)
}
