package io.github.samples.myapplication

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext

open class BaseActivity : ComponentActivity() {
    private lateinit var topActivityCash : Activity

    override fun onResume() {
        super.onResume()
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}