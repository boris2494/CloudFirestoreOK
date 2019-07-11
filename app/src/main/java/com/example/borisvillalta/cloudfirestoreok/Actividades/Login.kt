package com.example.borisvillalta.cloudfirestoreok.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    var usuario: EditText? = null
    var contrasena: EditText? = null
    var progressBar: ProgressBar? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usuario = findViewById(R.id.etEmailLogin)
        contrasena = findViewById(R.id.etContrasenaLogin)
        progressBar = findViewById(R.id.progressBarLogin)
        auth = FirebaseAuth.getInstance()

    }

    fun forgotPassword(view: View){

    }

    fun login(view: View){
        loginUSer()

    }

    //Ir a registrar un nuevo usuario
    fun register(view: View){
        startActivity(Intent(this, RegistrarNuevoUsuario::class.java))
    }

    fun loginUSer(){
        val user = usuario?.text.toString()
        val contra = contrasena?.text.toString()

        //comprobar que los campos no esten vacios
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(contra)){
            progressBar?.visibility = View.VISIBLE

            //ingresar
            auth?.signInWithEmailAndPassword(user,contra)
                    ?.addOnCompleteListener(this){
                        task ->

                        // aqui luego debe de ir ingresar donde se muestre la informacion de compras
                        if (task.isSuccessful){
                            startActivity(Intent(this, ArticulosALaVenta::class.java))

                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                        }
                    }


        progressBar?.visibility = View.GONE

        }
    }
}
