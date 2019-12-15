package com.example.todo.ui.shopsAdd

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.todo.R
import com.example.todo.ShopsActivity
import com.example.todo.broadcastreceivers.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import todo_database.FirebaseShopDb
import todo_database.Shop

class ShopsAddFragment : Fragment() {

    private var geofencePendingIntent: PendingIntent? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shops_add, container, false)
        val saveButton: Button = root.findViewById(R.id.shopAddSaveButton)

        saveButton.setOnClickListener{ onAddButtonClick() }

        return root
    }

    private fun onAddButtonClick() {
        val shopsActivity: ShopsActivity = activity as ShopsActivity

        val name: String = shopsActivity.findViewById<EditText>(R.id.shopAddNameEditText).text.toString()
        val description: String = shopsActivity.findViewById<EditText>(R.id.shopAddDescriptionEditText).text.toString()
        val radiusString: String = shopsActivity.findViewById<EditText>(R.id.shopAddRadiusEditText).text.toString()
        val coordinates: String = shopsActivity.findViewById<TextView>(R.id.shopAddCoordinatesTextView).text.toString()

        val error = validateFields(name, description, radiusString, coordinates)

        if (error != null) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
        else {
            val radius: Float = radiusString.toFloat()

            val shopToAdd = Shop(null, name, description, radius, coordinates)

            val shopDb = FirebaseShopDb()
            shopDb.addShop(shopToAdd)

            addGeofence(shopToAdd.id!!, name, radius, coordinates)

            goToListFragment(shopsActivity)
        }
    }

    private fun validateFields(name: String, description: String, radiusString: String, coordinates: String): String? {

        if (name.isEmpty()) return "Nazwa jest wymagana"
        if (description.isEmpty()) return "Opis jest wymagany"
        if (radiusString.isEmpty()) return "Promień jest wymagany"
        if (radiusString.toDouble() <= 0) return "Promień musi być większy od 0"
        if (coordinates.isEmpty()) return "Koordynaty są wymagane"

        return null
    }

    private fun addGeofence(geofenceId: String, name: String, radius: Float, coordinates: String) {
        val lat = coordinates.split(",")[0].trim().toDouble()
        val lng = coordinates.split(",")[1].trim().toDouble()
        val latLng = LatLng(52.230263, 21.010747)

        var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context!!)

        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(
                    latLng.latitude,
                    latLng.longitude,
                    radius
            )
            .setExpirationDuration(1000*60*60*24) // 24h
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        geofencingClient.addGeofences(getGeofencingRequest(geofence), getGeofencePendingIntent(name)).run {
            addOnSuccessListener {
                Log.i("GEOFENCE_add_success", "Pomyślnie dodano geofence dla sklepu $name")
            }
            addOnFailureListener {
                it.printStackTrace()
                Log.i("GEOFENCE_add_failure", "Błąd przy dodawaniu geofence dla sklepu $name.")
            }
        }
    }

    private fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofence(geofence)
        }.build()
    }

    private fun getGeofencePendingIntent(name: String): PendingIntent {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent!!
        }

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.putExtra(getString(R.string.intent_extra_geofence_shop_name), name)
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return geofencePendingIntent!!
    }

    private fun goToListFragment(activity: Activity) {
        activity.onBackPressed()
    }
}