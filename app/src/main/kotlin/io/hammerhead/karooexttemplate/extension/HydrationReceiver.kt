package io.hammerhead.karooexttemplate.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.hammerhead.karooexttemplate.models.HydrationManager

class HydrationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_LOG_SIP) {
            HydrationManager.logSip()
        } else if (intent?.action == ACTION_LOG_BOTTLE) {
            HydrationManager.logBottle()
        }
    }

    companion object {
        const val ACTION_LOG_SIP = "io.hammerhead.karooexttemplate.ACTION_LOG_SIP"
        const val ACTION_LOG_BOTTLE = "io.hammerhead.karooexttemplate.ACTION_LOG_BOTTLE"
    }
}
