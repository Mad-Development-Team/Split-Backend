package com.madteam.repository

import com.google.firebase.database.FirebaseDatabase

private const val LAST_UPDATED_GROUPS = "lastUpdatedGroups"

class RealtimeRepository {
    fun updateGroupRealtime(groupId: Int){
        val dbReference = FirebaseDatabase.getInstance().reference
        val currentTimeInMillis = System.currentTimeMillis().toString()
        val data = mapOf(groupId to currentTimeInMillis)

        dbReference.child(LAST_UPDATED_GROUPS).child(groupId.toString()).setValueAsync(data)
    }
}