package com.example.sathchaloodriver.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sathchaloodriver.R
import com.example.sathchaloodriver.Utilities.Util
import com.google.android.material.card.MaterialCardView


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

    //name
    private lateinit var userNameTextView:TextView

    //contact number
    private lateinit var driverNumberTextView:TextView

    //logout card
    private lateinit var logoutCard: MaterialCardView

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
        imageViewDisplayPicture = root.findViewById(R.id.imageViewUserDisplay)
        imageViewDisplayPicture.setOnClickListener { launchGallery() }
        if(Util.getGlobals().userImage != null){
            imageViewDisplayPicture.setImageBitmap(Util.getGlobals().userImage)
        }

        //display name
        userNameTextView = root.findViewById(R.id.userNameTextView)
        userNameTextView.text = Util.getGlobals().user!!.displayName

        //contact number
        driverNumberTextView = root.findViewById(R.id.driverNumberTextView)
        driverNumberTextView.text = activity!!.getPreferences(Context.MODE_PRIVATE)
            .getString("contactNumber", "")

        //logout
        logoutCard = root.findViewById(R.id.materialCardViewLogout)
        logoutCard.setOnClickListener {
            startActivity(Util.logout(root.context))
        }

        return root
    }

    private fun launchGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, Util.getImageRequest())
    }


}
