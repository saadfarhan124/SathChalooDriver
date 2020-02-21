package com.example.sathchaloodriver.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.example.sathchaloodriver.adapters.NotificationAdapter
import com.example.sathchaloodriver.dataModels.NotificationDataModel

class NotificationsFragment : Fragment() {

    private lateinit var root:View
    private lateinit var listOfNotification: MutableList<NotificationDataModel>
    private lateinit var mRecyclerView: RecyclerView
    private  lateinit var shimmerRecyclerView: ShimmerRecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        notificationsViewModel =
//            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
         root = inflater.inflate(R.layout.fragment_notifications, container, false)
        shimmerRecyclerView = root.findViewById(R.id.shimmer_recycler_view)

        loadNotification()
        return root
    }

        private fun loadNotification(){
            shimmerRecyclerView.visibility = View.VISIBLE
            listOfNotification = mutableListOf()
            Util.getFireStoreInstance().collection("driverNotification")
                .whereEqualTo("userID", Util.getGlobals().user!!.uid)
                .get()
                .addOnSuccessListener {

                    for(document in it.documents){

                        val notification = document.toObject(NotificationDataModel::class.java)
                        listOfNotification.add(notification!!)
                    }
                    mRecyclerView = root.findViewById(R.id.notificationRecyclerView)
                    mRecyclerView.layoutManager = LinearLayoutManager(root.context)
                    mRecyclerView.adapter = NotificationAdapter(listOfNotification)
                    shimmerRecyclerView.visibility = View.INVISIBLE
                }
        }

    }
