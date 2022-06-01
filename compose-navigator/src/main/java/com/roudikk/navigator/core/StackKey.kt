package com.roudikk.navigator.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Used as a key for a [NavigationStack], extend to define custom navigation keys.
 */
@Parcelize
open class StackKey : Parcelable

/**
 * Default Stack Key
 */
@Parcelize
internal object DefaultStackKey : StackKey()
