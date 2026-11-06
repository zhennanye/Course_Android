package com.yezhennan.exp04

import android.content.Context
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

object AppStorage {
    private const val TAG = "AppStorage"

    fun save(context: Context, fileName: String, value: Serializable) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                ObjectOutputStream(output).use { it.writeObject(value) }
            }
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to save $fileName", exception)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> load(context: Context, fileName: String): T? {
        return try {
            context.openFileInput(fileName).use { input ->
                ObjectInputStream(input).use { it.readObject() as? T }
            }
        } catch (_: FileNotFoundException) {
            null
        } catch (exception: IOException) {
            Log.e(TAG, "Unable to load $fileName", exception)
            null
        } catch (exception: ClassNotFoundException) {
            Log.e(TAG, "Invalid saved object in $fileName", exception)
            null
        }
    }
}
