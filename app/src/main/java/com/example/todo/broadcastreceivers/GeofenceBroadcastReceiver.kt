package com.example.todo.broadcastreceivers

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todo.CHANNEL_ID
import com.example.todo.R
import com.example.todo.ui.shopsMap.ShopsMapFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            Log.e("GeofenceBR_error_event", geofencingEvent.errorCode.toString())
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            val name = intent!!.getStringExtra(context?.getString(R.string.intent_extra_geofence_shop_name))

            sendNotification(name, context!!)
            Log.i("GeofenceBR_notif_sent", "Wysłano notyfikację z (Shop)GeofenceBroadcastReceivera dla: " + name)
        } else {
            // Log the error.
            Log.e("GeofenceBR_error_trans", "Nieprawidłowy transition type")
        }
    }

    private fun sendNotification(name: String, context: Context) {
        val natificationTitle = "Witaj w sklepie $name!"
        val notificationText = "Miło Cię znowu widzieć."

        val goToMapIntent = Intent(context, ShopsMapFragment::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, goToMapIntent, 0)

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_place_black_24dp)
                .setContentTitle(natificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }
}