package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.activities.DetailActivity

const val NOTIFICATION_ID = 0
const val INTENT_STATUS_KEY = "status"
const val INTENT_FILE_KEY = "file_name"

fun NotificationManager.sendNotification(messageBody : String, applicationContext: Context, status : String, fileName : String) {

    val detailScreenIntent = Intent(applicationContext, DetailActivity::class.java)
    detailScreenIntent.putExtra(INTENT_STATUS_KEY, status)
    detailScreenIntent.putExtra(INTENT_FILE_KEY, fileName)

    val buttonIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        detailScreenIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.button_text))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            "details"
            ,buttonIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)

    notify(NOTIFICATION_ID, builder.build())
}