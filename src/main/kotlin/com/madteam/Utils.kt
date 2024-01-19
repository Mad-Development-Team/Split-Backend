package com.madteam

import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DEFAULT_LENGTH_INVITE_CODE = 6
private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun generateInviteCode(length: Int = DEFAULT_LENGTH_INVITE_CODE): String {
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..length)
        .map { Random().nextInt(characters.length) }
        .map(characters::get)
        .joinToString("")
}

fun getCurrentDateTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    return current.format(formatter)
}

fun getRandomHexColor(): String {
    val random = Random()
    val nextInt = random.nextInt(0xffffff + 1)
    return String.format("0x%08X", nextInt)
}