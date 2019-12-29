package com.example.sathchaloodriver.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sathchaloodriver.R


class ProfileFragment: Fragment(){

    //icons
    private lateinit var imageViewNumberIcon: ImageView
    private lateinit var imageViewEmailIcon: ImageView
    private lateinit var imageViewPasswordIcon: ImageView
    private lateinit var imageViewGenderIcon: ImageView
    private lateinit var imageViewDobIcon: ImageView
    private lateinit var imageViewLogoutIcon: ImageView

    //display picture
    private lateinit var imageViewDisplayPicture: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        //setting profile icons
        imageViewNumberIcon = root.findViewById(R.id.img_num)
        imageViewNumberIcon.setImageResource(R.drawable.ic_phone)

        imageViewEmailIcon = root.findViewById(R.id.img_email)
        imageViewEmailIcon.setImageResource(R.drawable.ic_email)

        imageViewPasswordIcon = root.findViewById(R.id.img_pas)
        imageViewPasswordIcon.setImageResource(R.drawable.ic_changepass)

        imageViewGenderIcon = root.findViewById(R.id.img_gen)
        imageViewGenderIcon.setImageResource(R.drawable.ic_gender)

        imageViewDobIcon = root.findViewById(R.id.img_dob)
        imageViewDobIcon.setImageResource(R.drawable.ic_dob)

        imageViewLogoutIcon = root.findViewById(R.id.img_logout)
        imageViewLogoutIcon.setImageResource(R.drawable.ic_logout)

        //profile picture

        return root
    }


}
