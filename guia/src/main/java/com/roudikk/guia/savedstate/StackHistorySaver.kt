package com.roudikk.guia.savedstate

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.roudikk.guia.backstack.navhost.StackHistoryEntry
import kotlinx.parcelize.Parcelize

internal fun stackHistorySaver() = Saver<
    SnapshotStateList<StackHistoryEntry>,
    StackHistoryState
    >(
    save = { StackHistoryState(it.toList()) },
    restore = { SnapshotStateList<StackHistoryEntry>().apply { addAll(it.entries) } }
)

@Parcelize
internal data class StackHistoryState(
    val entries: List<StackHistoryEntry>
) : Parcelable
