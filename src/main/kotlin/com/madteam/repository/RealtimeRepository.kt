package com.madteam.repository

import com.google.firebase.database.FirebaseDatabase

private const val LAST_UPDATED_GROUPS = "lastUpdatedGroups"

class RealtimeRepository {
    fun updateGroupRealtime(groupId: Int){
        try {
            val dbReference = FirebaseDatabase.getInstance("https://split-app-spa-default-rtdb.europe-west1.firebasedatabase.app").reference
            val currentTimeInMillis = System.currentTimeMillis().toString()
            val data = mapOf(groupId.toString() to currentTimeInMillis)

            dbReference.child(LAST_UPDATED_GROUPS).child(groupId.toString()).setValueAsync(data)
        } catch (e: Exception) {
            println("Error updating group realtime: $e")
        }
    }
}