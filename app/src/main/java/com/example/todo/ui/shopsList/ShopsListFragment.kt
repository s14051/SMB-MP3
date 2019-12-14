package com.example.todo.ui.shopsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import todo_database.Shop

class ShopsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shops_list, container, false)

        val shopsList = listOf(
                Shop("x-kom", "Polecam Maciej Sadoś", 2.0, "1234 1234 454545"),
                Shop("Morele.net", "Dobry sklep", 5.0, "334234 232.0902")
        )

        viewManager = LinearLayoutManager(root.context)
        viewAdapter = ShopsListAdapter(shopsList)

        recyclerView = root.findViewById<RecyclerView>(R.id.shops_list_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


        return root
    }
}