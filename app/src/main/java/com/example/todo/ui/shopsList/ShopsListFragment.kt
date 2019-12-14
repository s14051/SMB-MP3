package com.example.todo.ui.shopsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import todo_database.FirebaseShopDb
import todo_database.Shop

class ShopsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var shopsList: MutableList<Shop> = ArrayList()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shops_list, container, false)

        val shopsList = listOf(
                Shop(null, "x-kom", "Polecam Maciej Sadoś", 2.0, "1234 1234 454545"),
                Shop(null, "Morele.net", "Dobry sklep", 5.0, "334234 232.0902")
        )

        viewManager = LinearLayoutManager(root.context)
        viewAdapter = ShopsListAdapter(shopsList)

        recyclerView = root.findViewById<RecyclerView>(R.id.shops_list_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        initShopsList()

        return root
    }

    interface DbChangeCallback {
        fun addShop(newShop: Shop)
        fun deleteShop(shopId: String)
        fun updateShop(updatedShop: Shop)
    }

    fun initShopsList(){
        val dcc = object : DbChangeCallback {
            override fun addShop(newShop: Shop) {
                shopsList.add(newShop)
                viewAdapter.notifyDataSetChanged()
            }

            override fun deleteShop(id: String) {
                val shopToRemove = shopsList.find { s -> s.id.equals(id) }
                shopsList.remove(shopToRemove)
                viewAdapter.notifyDataSetChanged()
            }

            override fun updateShop(updatedShop: Shop) {
                val shopToUpdate = shopsList.find { s -> s.id.equals(updatedShop.id) }

                shopToUpdate?.name = updatedShop.name
                shopToUpdate?.description = updatedShop.description
                shopToUpdate?.radius = updatedShop.radius
                shopToUpdate?.coordinates = updatedShop.coordinates

                viewAdapter.notifyDataSetChanged()
            }
        }

        val shopDb = FirebaseShopDb()
        shopDb.initFirebaseListeners(dcc)
    }
}