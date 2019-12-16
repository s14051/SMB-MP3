package com.example.todo.ui.shopsMap

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todo.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import todo_database.Shop


class ShopsMapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shops_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        configureCurrentLocationUI()
        addShopsGeofencesFromDb()
    }

    private fun configureCurrentLocationUI() {
        try {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    private fun addShopsGeofencesFromDb() {
        val _db: FirebaseDatabase = FirebaseDatabase.getInstance()
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference = _db.getReference("users").child(user?.uid!!).child("shops")

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val shop = dataSnapshot.getValue(Shop::class.java)!!
                displayGeofence(shop)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun displayGeofence(shop: Shop) {
        val lat = shop.coordinates.split(",")[0].trim().toDouble()
        val lng = shop.coordinates.split(",")[1].trim().toDouble()
        val shopLatLng = LatLng(lat, lng)
        Log.i("SHOP_COORDS", "${shop.name}: ${shop.coordinates}")

        val circleOptions = CircleOptions()
                .center(shopLatLng)
                .radius(shop.radius.toDouble())
                .fillColor(Color.parseColor("#8cff4343"))
                .strokeColor(Color.parseColor("#e0ff3c3c"))

        val markerOptions = MarkerOptions()
                .position(shopLatLng)
                .title(shop.name)
                .snippet(shop.description)

        map.addCircle(circleOptions)
        map.addMarker(markerOptions)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLatLng, 13f))
    }
}