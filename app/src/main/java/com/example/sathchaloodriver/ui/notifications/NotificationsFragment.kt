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
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.example.sathchaloodriver.adapters.NotificationAdapter
import com.example.sathchaloodriver.dataModels.NotificationDataModel

class NotificationsFragment : Fragment() {

//    private lateinit var notificationsViewModel: NotificationsViewModel
    private lateinit var root:View
private lateinit var listOfNotification: MutableList<NotificationDataModel>
private lateinit var mRecyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        notificationsViewModel =
//            ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
//        val textView: TextView = root.findViewById(R.id.text_notifications)
//        notificationsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
//        mRecyclerView = root.findViewById(R.id.notificationRecyclerView)
//        mRecyclerView.layoutManager = LinearLayoutManager(root.context)
//        mRecyclerView.adapter = NotificationAdapter()
        loadNotification()
        return root
    }

        private fun loadNotification(){
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

                }
        }

    }
