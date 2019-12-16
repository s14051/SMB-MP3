package com.example.todo.broadcastreceivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo.CHANNEL_ID
import com.example.todo.R
import com.example.todo.ui.shopsMap.ShopsMapFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    var id = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            Log.e("GeofenceBR_error_event", geofencingEvent.errorCode.toString())
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (isTransitionEnter(geofenceTransition) || isTransitionExit(geofenceTransition)) {
            val shopName = intent!!.getStringExtra(context?.getString(R.string.intent_extra_geofence_shop_name))
            var notificationTitle = "Witaj w sklepie $shopName!"
            var notificationText = "Miło Cię znowu widzieć."

            if (isTransitionExit(geofenceTransition)){
                notificationTitle = "Dziękujemy, że wpadłeś do $shopName"
                notificationText = "Do zobaczenia!"
            }

            sendNotification(notificationTitle, notificationText, context!!)
            Log.i("GeofenceBR_notif_sent", "Wysłano notyfikację z (Shop)GeofenceBroadcastReceivera dla: " + shopName)
        } else {
            // Log the error.
            Log.e("GeofenceBR_error_trans", "Nieprawidłowy transition type")
        }
    }

    private fun isTransitionEnter(geofenceTransition: Int) : Boolean {
        return geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
    }

    private fun isTransitionExit(geofenceTransition: Int) : Boolean {
        return geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
    }

    private fun sendNotification(notificationTitle: String, notificationText: String, context: Context) {
        val goToMapIntent = Intent(context, ShopsMapFragment::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, goToMapIntent, 0)

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_place_black_24dp)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id++, builder.build())
        }
    }
}