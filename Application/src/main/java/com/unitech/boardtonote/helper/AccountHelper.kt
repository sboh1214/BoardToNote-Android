package com.unitech.boardtonote.helper

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.unitech.boardtonote.adapter.ListCloudAdapter
import java.io.File

object AccountHelper
{
    var user: FirebaseUser? = null
        get() = FirebaseAuth.getInstance().currentUser

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

        fun onSignOut(context: Context, adapter: ListCloudAdapter)
        {
            adapter.btnCloudList.cloudList.forEach {
                File(it.dirPath).deleteRecursively()
                File(it.dirPath).delete()
                adapter.btnCloudList.cloudList.remove(it)
            }
            adapter.notifyDataSetChanged()
        }
    }
}