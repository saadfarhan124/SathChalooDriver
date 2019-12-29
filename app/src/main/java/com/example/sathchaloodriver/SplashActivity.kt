package com.example.sathchaloodriver

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import com.example.sathchaloodriver.Utilities.Util
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import java.io.File


class SplashActivity: AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long=4000 // 1 sec

    //Widgets and helpers
    private lateinit var splashLogo: ImageView
    private var user:FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Function to initiate animation
        animation()
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            initialization()
            goToAfterSplash()


            // close this activity

        }, SPLASH_TIME_OUT)
    }

    private fun animation(){
        //animation
        splashLogo = findViewById(R.id.splash_logo)
        var anim = ScaleAnimation(0.5f, 1f, .5f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.duration = 2000
        anim.fillAfter = true
        splashLogo.startAnimation(anim)
    }

    private fun initialization(){
        //Check user state
        val homeScreen = Intent(applicationContext,MainActivity::class.java)
        val login = Intent(applicationContext,LoginActivity::class.java)
        user = Util.getFirebaseAuth().currentUser
        if(user != null) {
            Util.getGlobals().user = user
            if(getPreferences(Context.MODE_PRIVATE).getString("ImageUri${user!!.uid}","")!!.isNotEmpty()){
                Util.getGlobals().userImage = BitmapFactory.decodeFile(getPreferences(Context.MODE_PRIVATE).getString("ImageUri${user!!.uid}",""))
                startActivity(homeScreen)
            }else{
                val localFile = File.createTempFile("images", "jpg")
                Util.getStorageRef().getFile(localFile).addOnSuccessListener {
                    with(getPreferences(Context.MODE_PRIVATE).edit()){
                        putString("ImageUri${user!!.uid}", localFile.absolutePath)
                        commit()
                        Util.getGlobals().userImage = BitmapFactory.decodeFile(localFile.absolutePath)

                    }
                }.addOnFailureListener{
                    startActivity(homeScreen)
                }
            }

        }else{
            startActivity(login)
        }
    }

    private fun goToAfterSplash(){

    }
}