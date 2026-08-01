package com.mj.homelibrary.data

fun String.normalizedIsbn(): String =
    uppercase().filter { it.isDigit() || it == 'X' }

fun String.normalizedIsbn10OrNull(): String? {
    val normalized = normalizedIsbn()
    return normalized.takeIf {
        it.length == 10 &&
            it.take(9).all(Char::isDigit) &&
            (it.last().isDigit() || it.last() == 'X')
    }
}

fun String.normalizedIsbn13OrNull(): String? {
    val normalized = normalizedIsbn()
    return normalized.takeIf { it.length == 13 && it.all(Char::isDigit) }
}

fun String.validIsbnOrNull(): String? {
    val normalized = normalizedIsbn()
    return when {
        normalized.isValidIsbn13() -> normalized
        normalized.isValidIsbn10() -> normalized
        else -> null
    }
}

fun String.isValidIsbn(): Boolean = validIsbnOrNull() != null

private fun String.isValidIsbn10(): Boolean {
    if (length != 10 || !take(9).all(Char::isDigit) || !(last().isDigit() || last() == 'X')) return false
    val sum = mapIndexed { index, char ->
        val value = if (char == 'X') 10 else char.digitToInt()
        value * (10 - index)
    }.sum()
    return sum % 11 == 0
}

private fun String.isValidIsbn13(): Boolean {
    if (length != 13 || !all(Char::isDigit)) return false
    val sum = take(12).mapIndexed { index, char ->
        char.digitToInt() * if (index % 2 == 0) 1 else 3
    }.sum()
    val checkDigit = (10 - (sum % 10)) % 10
    return checkDigit == last().digitToInt()
}
