package com.example.sathchaloodriver

import android.animation.ValueAnimator
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.*
import com.example.sathchaloodriver.Utilities.Util
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
                    .addOnSuccessListener {
                        //If user is not a driver
                        if (it.data.isNullOrEmpty()) {
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
                            Util.getGlobals().user = Util.getFirebaseAuth().currentUser
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                        loginProgress.visibility = View.INVISIBLE


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
