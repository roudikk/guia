package com.roudikk.navigator.sample.feature.details.api

import android.os.Parcelable
import com.roudikk.navigator.core.NavigationKey
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class DetailsResult(val value: String) : Parcelable

@Parcelize
class DetailsKey(val item: String) : NavigationKey
