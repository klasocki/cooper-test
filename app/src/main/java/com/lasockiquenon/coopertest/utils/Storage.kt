package com.lasockiquenon.coopertest.utils

import android.content.Context

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import java.lang.reflect.Modifier
import java.util.*
import com.google.gson.GsonBuilder

class Storage {

    val PREFS_NAME = "RESULTS"
    val RESULTS = "Result"

    fun storeResults(context: Context, results: List<Results>) {
        // used for store arrayList in json format
        val editor: Editor
        val settings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = settings.edit()

        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        builder.excludeFieldsWithoutExposeAnnotation()

        val sExposeGson = builder.create()
        val jsonResults = sExposeGson.toJson(results)
        editor.putString(RESULTS, jsonResults)
        editor.apply()
    }

    fun loadResults(context: Context): ArrayList<Results>? {
        // used for retrieving arraylist from json formatted string
        val settings: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return if (settings.contains(RESULTS)) {
            val jsonResults = settings.getString(RESULTS, null)

            val builder = GsonBuilder()
            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            builder.excludeFieldsWithoutExposeAnnotation()

            val sExposeGson = builder.create()
            val resultItems = sExposeGson.fromJson(jsonResults, Array<Results>::class.java)
            ArrayList(listOf(*resultItems))
        } else
            null
    }

    fun addResult(context: Context, myModel: Results) {
        var results: MutableList<Results>? = loadResults(context)
        if (results == null)
            results = ArrayList()
        results.add(0, myModel)
        storeResults(context, results)
    }

}