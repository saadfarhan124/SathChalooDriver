package com.example.sathchaloodriver.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
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
        listOfRides = mutableListOf()
        Util.getFireStoreInstance().collection("DriverRides")
            .whereEqualTo("driverID", Util.getGlobals().user!!.uid)
            .get()
            .addOnSuccessListener {
                for(doc in it.documents){
                    val ride = doc.toObject(DriverRideDataModel::class.java)
                    listOfRides.add(ride!!)
                }
                mRecyclerView = root.findViewById(R.id.historyRecyclerView)
                mRecyclerView.layoutManager = LinearLayoutManager(root.context)
                mRecyclerView.adapter = HistoryAdapter(root.context, listOfRides)

            }.addOnFailureListener{
                Toast.makeText(root.context, it.message, Toast.LENGTH_SHORT).show()
            }

        return root
    }
}