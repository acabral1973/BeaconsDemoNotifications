package com.estimote.notification

import android.app.Application
import com.estimote.cloud_plugin.common.EstimoteCloudCredentials
import com.estimote.notification.estimote.NotificationsManager

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

class MyApplication : Application() {

    val cloudCredentials = EstimoteCloudCredentials("proximity-smartech-es-s-no-bcj", "e63d9843091bc9a4ed269507fa70ac2a")

    lateinit var notificationsManager: NotificationsManager

    override fun onCreate() {
        super.onCreate()
        notificationsManager = NotificationsManager(this)
    }

    fun enableBeaconNotifications() {
        notificationsManager.startMonitoring()
    }

}
