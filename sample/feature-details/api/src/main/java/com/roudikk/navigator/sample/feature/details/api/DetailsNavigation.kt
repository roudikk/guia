package com.roudikk.guia.sample.feature.details.api

import android.os.Parcelable
import com.roudikk.guia.core.NavigationKey
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
value class DetailsResult(val value: String) : Parcelable

@Parcelize
class DetailsKey(val item: String) : NavigationKey
