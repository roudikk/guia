package com.roudikk.navigator.savedstate

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.roudikk.navigator.backstack.navhost.StackHistoryEntry

internal fun stackHistorySaver() = Saver<
    SnapshotStateList<StackHistoryEntry>,
    List<StackHistoryEntry>>(
    save = { it.toList() },
    restore = {
        SnapshotStateList<StackHistoryEntry>().apply {
            addAll(it)
        }
    }
)
