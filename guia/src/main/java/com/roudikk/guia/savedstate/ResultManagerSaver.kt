package com.roudikk.guia.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import com.roudikk.guia.core.NavigatorResultManager

/**
 * Used to save and restore the state of a [NavigatorResultManager].
 */
internal fun resultManagerSaver() = Saver<NavigatorResultManager, HashMap<String, Parcelable>>(
    save = { resultManager ->
        hashMapOf<String, Parcelable>().apply {
            resultManager.results
                .filter { it.value is Parcelable }
                .forEach { this[it.key] = it.value as Parcelable }
        }
    },
    restore = { savedResults ->
        NavigatorResultManager().apply {
            savedResults.forEach { results[it.key] = it.value }
        }
    }
)
