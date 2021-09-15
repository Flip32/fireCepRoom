package com.example.logingoogle.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class User constructor(var uid: String, var name: String? = "", var email: String? = "", @Exclude var isNew: Boolean = false ) : Serializable {
    companion object {
        val REF_NANE = "users"
    }
}