package com.mj.homelibrary.ui

import android.content.Context
import com.mj.homelibrary.R
import com.mj.homelibrary.data.LanguageCode
import com.mj.homelibrary.data.entity.LocationEntity
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date

fun LocationEntity?.displayBreadcrumb(context: Context): String {
    if (this == null) return context.getString(R.string.unknown_location)
    val separator = context.getString(R.string.breadcrumb_separator)
    return listOf(room, unit, shelf).joinToString(separator)
}

fun List<String>.displayAuthors(context: Context): String =
    if (isEmpty()) {
        context.getString(R.string.unknown_author)
    } else {
        joinToString(context.getString(R.string.multiple_authors_separator))
    }

fun Long?.displayDate(context: Context): String =
    this?.let { DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(it)) }
        ?: context.getString(R.string.date_not_set)

fun languageLabel(context: Context, code: String): String =
    context.getString(LanguageCode.fromCode(code).labelRes)

fun Double.displayCost(): String = NumberFormat.getCurrencyInstance().format(this)
