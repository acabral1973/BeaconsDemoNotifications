package com.estimote.notification.estimote

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.TextView
import com.estimote.notification.MainActivity
import com.estimote.notification.MyApplication
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder
import kotlinx.android.synthetic.main.activity_main.*
import com.estimote.proximity_sdk.trigger.ProximityTriggerBuilder
import java.util.*

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

class NotificationsManager(private val context: Context) : Observable() {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val helloText : String = "Welcome JACS!️"
    private val goodbyeText : String = "We will miss you!"
    private val helloNotification = buildNotification("Hello", helloText)
    private val goodbyeNotification = buildNotification("Bye bye", goodbyeText)

    var statusTitle : String = "walking down the street"

    private fun buildNotification(title: String, text: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(
                    "content_channel", "Things near you", NotificationManager.IMPORTANCE_HIGH))
        }

        return NotificationCompat.Builder(context, "content_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build()
    }

    fun startMonitoring() {
        val notificationId = 1
        val proximityObserver = ProximityObserverBuilder(context, (context as MyApplication).cloudCredentials)
                .withOnErrorAction { throwable ->
                    Log.e("app", "proximity observer error: " + throwable)
                }
                .withBalancedPowerMode()
                .build()

        val zone = proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("proximity-smartech-es-s-no-bcj", "example-proximity-zone")
                .inCustomRange(1.0)
                .withOnEnterAction {
                    notificationManager.notify(notificationId, helloNotification)
                    statusTitle = helloText

                    setChanged() //Inherited from Observable
                    notifyObservers(true)
                }
                .withOnExitAction {
                    notificationManager.notify(notificationId, goodbyeNotification)
                    statusTitle = goodbyeText

                    setChanged() //Inherited from Observable
                    notifyObservers(false)
                }
                .create()
        proximityObserver.addProximityZone(zone)
        proximityObserver.start()

        // on Android 8.0 and later, you can use the Proximity Trigger to trigger an intent ... or,
        // more relevant to this example, a notification ... even if the app is killed!
        //
        // read more about it on:
        // https://github.com/estimote/android-proximity-sdk#background-scanning-using-proximity-trigger-android-80

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            ProximityTriggerBuilder(context)
//                    .displayNotificationWhenInProximity(helloNotification)
//                    .triggerOnlyOnce()
//                    .withNotificationId(0)
//                    .build()
//                    .start()
//        }
    }

}
