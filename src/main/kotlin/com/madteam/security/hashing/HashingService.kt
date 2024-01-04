package com.madteam.security.hashing

private const val DEFAULT_SALT_LENGTH = 32

interface HashingService {
    fun generateSaltedHash(
        value: String,
        saltLength: Int = DEFAULT_SALT_LENGTH
    ): SaltedHash

    fun verify(
        value: String,
        saltedHash: SaltedHash
    ): Boolean
}