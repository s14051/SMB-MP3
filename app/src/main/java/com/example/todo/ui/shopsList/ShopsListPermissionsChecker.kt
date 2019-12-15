package com.example.todo.ui.shopsList

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todo.R

class ShopsListPermissionsChecker {
    private val neededPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION)

    private lateinit var activity: Activity

    fun checkPermissions(activity: Activity) {
        this.activity = activity

        if (!allPermissionGranted(neededPermissions)) {
            ActivityCompat.requestPermissions(activity,
                    neededPermissions,
                    activity.applicationContext.resources.getInteger(R.integer.shop_permissions_request_code))
        }
    }

    private fun allPermissionGranted(neededPermissions: Array<String>) : Boolean {
        return neededPermissions.all { p -> ContextCompat.checkSelfPermission(activity, p) == PackageManager.PERMISSION_GRANTED }
    }
}