package com.example.todo.ui.shopsAdd

import android.app.Activity
import android.os.Bundle
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
import todo_database.FirebaseShopDb
import todo_database.Shop

class ShopsAddFragment : Fragment() {

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
            val radius: Double = radiusString.toDouble()

            val shopToAdd = Shop(null, name, description, radius, coordinates)

            val shopDb = FirebaseShopDb()
            shopDb.addShop(shopToAdd)
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

    private fun goToListFragment(activity: Activity) {
        activity.onBackPressed()
    }
}