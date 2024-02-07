package com.madteam.data.table

import org.jetbrains.exposed.dao.id.IntIdTable

object BalanceTable : IntIdTable(
    name = "balance"
) {
    val payMemberId = integer("payMemberId")
    val groupId = integer("groupId")
    val amount = double("amount")
    val receiverMemberId = integer("receiverMemberId")
}