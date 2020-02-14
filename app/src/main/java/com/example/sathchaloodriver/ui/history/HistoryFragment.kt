package com.example.sathchaloodriver.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.adapters.HistoryAdapter
import com.example.sathchaloodriver.dataModels.DriverRideDataModel

class HistoryFragment : Fragment(){

//    private lateinit var walletViewModel: HistoryViewModel
private lateinit var mRecyclerView: RecyclerView
private lateinit var listOfRides: MutableList<DriverRideDataModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_history, container, false)
        mRecyclerView = root.findViewById(R.id.historyRecyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(root.context)
        mRecyclerView.adapter = HistoryAdapter()
        return root
    }
}