package com.estimote.notification


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.notification.R.id.*
import com.estimote.notification.estimote.NotificationsManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//
// Running into any issues? Drop us an email to: contact@estimote.com
//

class MainActivity : AppCompatActivity(), Observer {
    override fun update(p0: Observable?, p1: Any?) {
        when (p0){
            is NotificationsManager -> {
                if (p1 is Boolean){
                    setButtons(p1)
                    updateTextView(p0.statusTitle)

                }
            }
        }
    }

     lateinit var notifyTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notifyTextView = notify_textview

        val app = application as MyApplication

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        onRequirementsFulfilled = {
                            Log.d("beacons", "requirements fulfilled")
                            app.enableBeaconNotifications()
                            app.notificationsManager.addObserver(this)
                        },
                        onRequirementsMissing = { requirements ->
                            Log.e("beacons", "requirements missing: " + requirements)
                        },

                        onError = { throwable ->
                            Log.e("beacons", "requirements error: " + throwable)
                        })
    }

    private fun setButtons(p1: Boolean) {
        if (p1) {
            wifi_button.visibility = View.VISIBLE
            event_button.visibility = View.VISIBLE
            news_button.visibility = View.VISIBLE
        } else {
            wifi_button.visibility = View.INVISIBLE
            event_button.visibility = View.INVISIBLE
            news_button.visibility = View.INVISIBLE
        }
    }

    fun updateTextView(text: String) {
        notifyTextView.text = text
        Log.i("beacons", "se ha actualizado el texto: " + text)
    }
}

