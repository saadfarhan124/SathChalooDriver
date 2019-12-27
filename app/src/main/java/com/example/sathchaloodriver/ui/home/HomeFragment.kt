package com.example.sathchaloodriver.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sathchaloodriver.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
private var btn:Button?= null
//    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        homeViewModel =
//            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
btn = root.findViewById(R.id.button)
        btn!!.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.activity_pickup_bottomsheet, null)
            val dialog = BottomSheetDialog(root.context)
            dialog.setContentView(view)
            dialog.show()
        }

        return root
    }
}