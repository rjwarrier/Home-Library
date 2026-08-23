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

fun String.toEquivalentIsbn10(): String? {
    val norm = normalizedIsbn()
    if (norm.length == 10 && norm.isValidIsbn10()) return norm
    if (norm.length == 13 && norm.startsWith("978") && norm.isValidIsbn13()) {
        val core = norm.substring(3, 12)
        val sum = core.mapIndexed { index, c -> c.digitToInt() * (10 - index) }.sum()
        val remainder = (11 - (sum % 11)) % 11
        val checkChar = if (remainder == 10) 'X' else remainder.digitToChar()
        return core + checkChar
    }
    return null
}

fun String.toEquivalentIsbn13(): String? {
    val norm = normalizedIsbn()
    if (norm.length == 13 && norm.isValidIsbn13()) return norm
    if (norm.length == 10 && norm.isValidIsbn10()) {
        val core = "978" + norm.substring(0, 9)
        val sum = core.mapIndexed { index, c ->
            c.digitToInt() * if (index % 2 == 0) 1 else 3
        }.sum()
        val checkDigit = (10 - (sum % 10)) % 10
        return core + checkDigit
    }
    return null
}

fun String.isIndianIsbn(): Boolean {
    val norm = normalizedIsbn()
    return norm.startsWith("97881") || norm.startsWith("97893") ||
        (norm.length == 10 && (norm.startsWith("81") || norm.startsWith("93")))
}

