package com.roudikk.guia.savedstate

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.roudikk.guia.backstack.navhost.StackHistoryEntry

internal fun stackHistorySaver() = Saver<
    SnapshotStateList<StackHistoryEntry>,
    List<StackHistoryEntry>>(
    save = { ArrayList(it) },
    restore = { SnapshotStateList<StackHistoryEntry>().apply { addAll(it) } }
)
