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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class RegistrarNuevoUsuario : AppCompatActivity() {

    private var etNombre: EditText? = null
    private var etCorreo: EditText? = null
    private var etContrasena: EditText? = null
    private var progressBar: ProgressBar? = null
    private var dbReference: DatabaseReference? = null
    private var dataBase: FirebaseDatabase? = null
    private var auth: FirebaseAuth? = null
    private var cloudFirestore: FirebaseFirestore? = null
    var user:FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_nuevo_usuario)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etEmail)
        etContrasena = findViewById(R.id.etEmail)

        progressBar = findViewById(R.id.progressBar)



        dataBase = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        cloudFirestore = FirebaseFirestore.getInstance()

    }

    //Esta funcion se manda a llamar del xml en el boton
    fun register(view: View){
        crearNuevoUsuario()
    }

       fun crearNuevoUsuario(){
       //Datos ingresados para la creacion del usuario
       val nombre = etNombre?.text.toString()
       val correo = etCorreo?.text.toString()
       val contrasena = etContrasena?.text.toString()

       //Verificar que los campos no esten vacios
       if (!TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contrasena)){
           progressBar?.visibility = View.VISIBLE

           //Creacion del nuevo usuario
           auth?.createUserWithEmailAndPassword(correo, contrasena)
                   ?.addOnCompleteListener(this){
                       //Resultado de la tarea
                       task ->
                      if (task.isComplete){
                           user=auth?.currentUser
                           //Enviar email de verificacion
                           verifyEmail(user)

                          var baseUsuario = cloudFirestore?.document("usuarios/usuario")

                          val usuarioId = HashMap<String, Any>()
                          usuarioId.put("UID", user?.uid.toString())

                          val nombreUsuario = HashMap<String, Any>()
                          nombreUsuario.put("Nombre", nombre)

                      /*    cloudFirestore?.collection("usuarios")?.document(user?.uid.toString())?.collection("agregadoAlCarrito")?.document("DatosUSuario")
                                  ?.set(nombreUsuario)?.addOnSuccessListener {
                                      void: Void? -> irArticulosALaVenta()
                                  }
*/
                          irArticulosALaVenta()
                       }
                   }
       }

   }

    fun irArticulosALaVenta(){
        startActivity(Intent(this, ArticulosALaVenta::class.java))
    }

    //Verificar enviando correo al email del nuevo usuario
    fun verifyEmail(user: FirebaseUser?){
        user?.sendEmailVerification()
                ?.addOnCompleteListener(this){
                    task ->

                    if (task.isComplete){
                        Toast.makeText(this, "Email enviado", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Error al registrarse", Toast.LENGTH_LONG).show()
                    }
                }
    }


}
