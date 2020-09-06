package com.unitech.boardtonote.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BtnLocalTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun createBtnLocal() {
        assertNotNull(BtnLocal(context, null))
    }
}