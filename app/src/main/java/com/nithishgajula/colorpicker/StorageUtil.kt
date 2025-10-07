package com.nithishgajula.colorpicker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object StorageUtil {
    private const val FILE_NAME = "colors.json"

    fun saveColors(context: Context, colors: List<ColorItem>) {
        val json = Gson().toJson(colors)
        val file = File(context.filesDir, FILE_NAME)
        file.writeText(json)
    }

    fun loadColors(context: Context): MutableList<ColorItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return mutableListOf()
        val json = file.readText()
        val type = object : TypeToken<MutableList<ColorItem>>() {}.type
        return Gson().fromJson(json, type)
    }
}
