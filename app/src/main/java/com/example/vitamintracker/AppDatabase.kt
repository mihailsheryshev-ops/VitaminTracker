package com.example.vitamintracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VitaminStorage {

    private const val PREFS_NAME = "vitamins_prefs"
    private const val KEY_VITAMINS = "vitamins_list"
    private const val KEY_TAKEN = "vitamins_taken"
    private val gson = Gson()

    fun getAll(context: Context): MutableList<Vitamin> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_VITAMINS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Vitamin>>() {}.type
        return gson.fromJson(json, type)
    }

    fun save(context: Context, vitamins: List<Vitamin>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_VITAMINS, gson.toJson(vitamins)).apply()
    }

    fun add(context: Context, vitamin: Vitamin) {
        val list = getAll(context)
        list.add(vitamin)
        save(context, list)
    }

    fun delete(context: Context, id: String) {
        val list = getAll(context)
        list.removeAll { it.id == id }
        save(context, list)
        val taken = getTakenToday(context).toMutableMap()
        taken.remove(id)
        saveTaken(context, taken)
    }

    private fun todayKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getTakenToday(context: Context): Map<String, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("${KEY_TAKEN}_${todayKey()}", null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveTaken(context: Context, taken: Map<String, Int>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("${KEY_TAKEN}_${todayKey()}", gson.toJson(taken)).apply()
    }

    fun markTaken(context: Context, vitaminId: String, doseMg: Float) {
        val taken = getTakenToday(context).toMutableMap()
        val current = taken[vitaminId] ?: 0
        taken[vitaminId] = current + doseMg.toInt()
        saveTaken(context, taken)
    }
}