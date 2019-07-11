package com.example.borisvillalta.cloudfirestoreok.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import com.example.borisvillalta.cloudfirestoreok.Adaptadores.AdaptadorRV
import com.example.borisvillalta.cloudfirestoreok.Clases.Articulo
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class ArticulosALaVenta : AppCompatActivity() {

    var toolbar:android.support.v7.widget.Toolbar? = null
    var rv: RecyclerView? = null
    var layoutManager: LinearLayoutManager? = null
    var adaptadorRecycler: AdaptadorRV? = null
    var dbFirestore: FirebaseFirestore? = null
    var articulosLLeno: ArrayList<Articulo>? = null
    var progressB:ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos_ala_venta)

        dbFirestore = FirebaseFirestore.getInstance()
        articulosLLeno = ArrayList()
        progressB = findViewById(R.id.progressBarArticuloALaVenta)

        // Insertar Toolbar
        inicializarActionBar()

        main()
    }

    fun inicializarActionBar(){
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun implementarRecyclerView(articulos:ArrayList<Articulo>){
        if (articulos.isEmpty()){
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
        }
        rv = findViewById(R.id.rv)
        rv?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        rv?.layoutManager = layoutManager
        adaptadorRecycler = AdaptadorRV(articulos)
        rv?.adapter = adaptadorRecycler
    }

  private suspend fun agregarArregloArticulos1():ArrayList<Articulo>{

      dbFirestore?.collection("productos")
              ?.get()
              ?.addOnCompleteListener(object: OnCompleteListener<QuerySnapshot>{
                  override fun onComplete(task: Task<QuerySnapshot>) {

                     if (task.isSuccessful){

                         for (document in task.result!!){
                             var nombre = document["nombre"].toString()
                             var descripcion = document["descripcion"].toString()
                             var imagen = document["imagen"].toString()
                             var precio = document["precio"].toString()

                             articulosLLeno?.add(Articulo(nombre, descripcion, precio, imagen))
                         }

                     }
                  }

              })?.await()

      return  articulosLLeno!!

    }

    fun main(){GlobalScope.launch(Dispatchers.Main){

        progressB?.visibility = View.VISIBLE
        val articulo = withContext(Dispatchers.IO){agregarArregloArticulos1()}


        implementarRecyclerView(articulo)
        progressB?.visibility = View.GONE

    }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = MenuInflater(this).inflate(R.menu.menu_articulo_venta, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){

            R.id.iCarritoCompra ->{
                startActivity(Intent(this, Compras::class.java))
            }

            R.id.iCerrarSesionVenta ->{
                var auth = FirebaseAuth.getInstance()
                auth?.signOut()
                startActivity(Intent(this, Login::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
