package com.unitech.boardtonote.helper

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AccountHelper
{
    var user: FirebaseUser? = null

    val userName: String?
        get() = user?.displayName

    val email: String?
        get() = user?.email

    val photoUrl: Uri?
        get() = user?.photoUrl

    val uid: String?
        get() = user?.uid


    interface AccountInterface
    {
        fun requestSignIn()
        fun onSignIn()
        {
            user = FirebaseAuth.getInstance().currentUser
        }

        fun onSignOut()
    }
}