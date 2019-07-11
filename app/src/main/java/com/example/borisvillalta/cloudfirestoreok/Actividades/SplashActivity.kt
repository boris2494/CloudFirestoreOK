package com.example.borisvillalta.cloudfirestoreok.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, Login::class.java))
        }else{
            startActivity(Intent(this, ArticulosALaVenta::class.java))
        }
        finish()
    }
}
