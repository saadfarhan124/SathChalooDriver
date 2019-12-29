package com.example.sathchaloodriver

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import com.example.sathchaloodriver.Utilities.Util
import com.google.firebase.auth.UserProfileChangeRequest
import org.jetbrains.anko.onClick

class LoginActivity : AppCompatActivity() {

    //Widgets
    private lateinit var emailTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var loginButton: Button
    private lateinit var loginProgress: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialization()
    }

    private fun initialization() {
        emailTextView = findViewById(R.id.emailTextView)
        passwordTextView = findViewById(R.id.passwordTextView)
        loginProgress = findViewById(R.id.loginProgressBar)

        loginButton = findViewById(R.id.loginButton)
        loginButton.onClick {
            loginProgress.visibility = View.VISIBLE
            //Function to authenticate users
            Util.getFirebaseAuth().signInWithEmailAndPassword(
                emailTextView.text.toString(),
                passwordTextView.text.toString()
            ).addOnSuccessListener {
                //Function to verify that the user is a driver
                Util.getFireStoreInstance()
                    .collection("drivers")
                    .document(Util.getFirebaseAuth().uid!!)
                    .get()
                    .addOnSuccessListener {driverData ->
                        //If user is not a driver
                        if (driverData.data.isNullOrEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "Not a valid driver",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            emailTextView.requestFocus()
                            emailTextView.text = ""
                            passwordTextView.text = ""
                        }
                        //if the user is a driver
                        else {
//                            if (Util.getFirebaseAuth().currentUser!!.displayName.isNullOrEmpty()) {
                                //Updating user display name if not present

                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(driverData.data!!["name"].toString())
                                    .build()
                                Util.getFirebaseAuth().currentUser!!.updateProfile(profileUpdates)
                                    .addOnSuccessListener {
                                        with(getPreferences(Context.MODE_PRIVATE).edit()){
                                            Log.d("DADADASD",driverData.data!!["contactNumber"].toString())
                                            putString("contactNumber", driverData.data!!["contactNumber"].toString())
                                            commit()
                                            Util.getGlobals().user = Util.getFirebaseAuth().currentUser
                                            val intent =
                                                Intent(applicationContext, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            loginProgress.visibility = View.INVISIBLE
                                            startActivity(intent)
                                        }
                                    }
//                            } else {
//                                Util.getGlobals().user = Util.getFirebaseAuth().currentUser
//                                val intent = Intent(applicationContext, MainActivity::class.java)
//                                loginProgress.visibility = View.INVISIBLE
//                                startActivity(intent)
//                            }
                        }
                    }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                loginProgress.visibility = View.INVISIBLE
                emailTextView.text = ""
                passwordTextView.text = ""
                emailTextView.requestFocus()
            }

        }

    }
}
