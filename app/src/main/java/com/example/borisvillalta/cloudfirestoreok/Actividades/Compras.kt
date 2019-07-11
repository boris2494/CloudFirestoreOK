package com.example.borisvillalta.cloudfirestoreok.Actividades

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.borisvillalta.cloudfirestoreok.Adaptadores.AdaptadorCompras
import com.example.borisvillalta.cloudfirestoreok.Clases.ArticuloCarritoCompra
import com.example.borisvillalta.cloudfirestoreok.Interfaz.InterfazDelete
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Compras : AppCompatActivity() {

    var toolbar:android.support.v7.widget.Toolbar? = null

    var dbFirestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null

    var arregloArticulo: ArrayList<ArticuloCarritoCompra>? = null
    var rvCompras: RecyclerView? = null
    var layoutManager: LinearLayoutManager? = null
    var adaptadorRecyclerCompras: AdaptadorCompras? = null

    var progressB: ProgressBar? = null
    var tvTotal: TextView ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compras)

        dbFirestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        progressB = findViewById(R.id.progressBarCompras)
        tvTotal = findViewById(R.id.tvTotal)
        arregloArticulo = ArrayList()

        inicializarActionBar()

        main()

    }

    fun inicializarActionBar(){
        toolbar = findViewById(R.id.toolbarCompras)
        setSupportActionBar(toolbar)
    }

    fun main(){
        GlobalScope.launch(Dispatchers.Main){

            progressB?.visibility = View.VISIBLE
            //Obtener la informacion desde cloudFirestore.
            val articulo = withContext(Dispatchers.IO){agregarArregloArticulos()}

            implementarRecyclerView(articulo)

            calcularTotal(articulo)

            progressB?.visibility = View.GONE

        }

    }

    private suspend fun agregarArregloArticulos():ArrayList<ArticuloCarritoCompra>{

        dbFirestore?.collection("usuarios")?.document(auth?.currentUser!!.uid)?.collection("agregadoAlCarrito")
                ?.get()
                ?.addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(task: Task<QuerySnapshot>) {

                        if (task.isSuccessful){
                            //Llenar el arreglo con la informacion desde cloudFirestore
                            for (document in task.result!!){
                                var nombre = document["nombre"].toString()
                                var cantidad = document["cantidad"].toString()
                                var imagen = document["imagen"].toString()
                                var precio = document["precio"].toString()

                                arregloArticulo?.add(ArticuloCarritoCompra(nombre, cantidad, precio, imagen))
                            }

                        }
                    }

                })?.await()

        return  arregloArticulo!!

    }

    fun implementarRecyclerView(articulos:ArrayList<ArticuloCarritoCompra>){
        if (articulos.isEmpty()){
            Toast.makeText(this, "No hay articulos selecionados", Toast.LENGTH_SHORT).show()
        }
        rvCompras = findViewById(R.id.rvCompras)
        rvCompras?.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        rvCompras?.layoutManager = layoutManager

        adaptadorRecyclerCompras = AdaptadorCompras(articulos, object : InterfazDelete {
            //Se recibe el index a traves de la interfaz para eliminar el articulo
            override fun articuloEliminado(index: Int) {
                super.articuloEliminado(index)
                articulos.remove(articulos[index])
                //Calcular el total luego de eliminar el articulo
                calcularTotal(articulos)
                adaptadorRecyclerCompras?.notifyDataSetChanged()
            }
        })

        rvCompras?.adapter = adaptadorRecyclerCompras

    }

    fun calcularTotal(arregloArticulo: ArrayList<ArticuloCarritoCompra>){

        var precioTotal = 0.0F


        for (precioPorArticulo in arregloArticulo){
            var precioFloatPorArticulo = precioPorArticulo.precio.toFloat()
          precioTotal = + precioFloatPorArticulo
        }
        tvTotal?.setText(precioTotal.toString())


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_compras, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.iCerrarCompras ->{
                auth?.signOut()

                startActivity(Intent(this, Login::class.java))

            }
        }
        return super.onOptionsItemSelected(item)
    }

}
