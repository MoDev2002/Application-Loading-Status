package com.udacity.activities

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.customViews.ButtonState
import com.udacity.R
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var fileName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))

        custom_button.setOnClickListener {
            when(findViewById<RadioButton>(radioGroup.checkedRadioButtonId)) {
                glide -> {
                    fileName = getString(R.string.glide_title)
                    download(GLIDE_URL, R.string.glide_title, R.string.glide_description)
                }
                retrofit -> {
                    fileName = getString(R.string.retrofit_title)
                    download(RETROFIT_URL, R.string.retrofit_title, R.string.retrofit_description)
                }
                loadApp -> {
                    fileName = getString(R.string.loadapp_title)
                    download(APP_URL, R.string.loadapp_title, R.string.loadapp_description)
                }
                else -> Toast.makeText(this, "Please Select File To Download", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.buttonState = ButtonState.Completed

            var downloadStatus = "Fail"

            val downloadManager = getSystemService(DownloadManager::class.java)

            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if(cursor.moveToFirst()){
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if(DownloadManager.STATUS_SUCCESSFUL == status){
                    downloadStatus = "Success"
                }
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.sendNotification(
                getString(R.string.notification_description),
                applicationContext,
                downloadStatus,
                fileName
            )
        }
    }

    private fun download(url: String, title: Int, description: Int) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(title))
                .setDescription(getString(description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        custom_button.buttonState = ButtonState.Loading
    }

    companion object {
        private const val APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://codeload.github.com/square/retrofit/zip/refs/heads/masterfails"
        private const val CHANNEL_ID = "channelId"
    }

    private fun createChannel(channelId : String, channelName : String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = "File download status"

            val notificationManager = this.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

}
