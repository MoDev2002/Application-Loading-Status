package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        //get the status and the file name from the notification intent
        val intentStatus = intent.getStringExtra(INTENT_STATUS_KEY)
        val intentFileName = intent.getStringExtra(INTENT_FILE_KEY)

        if (intentStatus == "Fail") status.setTextColor(Color.RED) else status.setTextColor(Color.GREEN)
        status.text = intentStatus
        fileName.text = intentFileName

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()

        detailButton.setOnClickListener {
            finish()
        }
    }

}
