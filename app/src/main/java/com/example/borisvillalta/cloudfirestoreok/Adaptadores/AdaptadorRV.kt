package com.example.borisvillalta.cloudfirestoreok.Adaptadores

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.borisvillalta.cloudfirestoreok.Clases.Articulo
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class AdaptadorRV (val articulos:ArrayList<Articulo>): RecyclerView.Adapter<AdaptadorRV.viewHolderCompras>() {

    var items:ArrayList<Articulo>? = null

    init {
        this.items = articulos
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderCompras {
        var vista = LayoutInflater.from(parent.context).inflate(R.layout.template_articulo, parent, false)
        var viewHolder = viewHolderCompras(vista, articulos)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: viewHolderCompras, position: Int) {
        var item = items?.get(position)

        holder.tvTitulo?.text = item?.nombre
        holder.tvPrecio?.text = item?.precio
        holder.tvDescripcion?.text = item?.descripcion
        Picasso.get().load(item?.imagen).into(holder.ivImagen)
        holder.clickEnAnadir()
    }

    class viewHolderCompras(vista: View, arregloArticulos:ArrayList<Articulo>): RecyclerView.ViewHolder(vista), View.OnClickListener {

        var contexto = vista.context
        var cloudFirebase = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()


        var tvTitulo: TextView? = null
        var tvDescripcion: TextView? = null
        var tvPrecio: TextView? = null
        var ivImagen: ImageView? = null
        var nCounter: ElegantNumberButton? = null
        var bAnadir: Button? = null
        var arregloArticulos: ArrayList<Articulo>? = null

        init {
            this.tvTitulo = vista.findViewById(R.id.tvTitulo)
            this.tvDescripcion = vista.findViewById(R.id.tvDescripcion)
            this.tvPrecio = vista.findViewById(R.id.tvPrecio)
            this.ivImagen = vista.findViewById(R.id.ivImagen)
            this.nCounter = vista.findViewById(R.id.nCounter)
            this.bAnadir = vista.findViewById(R.id.bAnadir)
            this.arregloArticulos = arregloArticulos

        }

        fun clickEnAnadir(){
            bAnadir?.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when(v?.id){
                R.id.bAnadir -> {
                    val articuloAnadir = HashMap<String, Any>()
                    articuloAnadir.put("nombre", tvTitulo?.text.toString())
                    //Se multiplica el precio por la cantidad
                    var precioPorCantidad = tvPrecio?.text.toString().toFloat() * nCounter?.number!!.toFloat()

                    articuloAnadir.put("precio", precioPorCantidad.toString())
                    articuloAnadir.put("imagen", arregloArticulos?.get(adapterPosition)!!.imagen)
                    articuloAnadir.put("cantidad", nCounter?.number.toString() )

                   cloudFirebase.collection("usuarios")?.document(auth.currentUser!!.uid)
                           ?.collection("agregadoAlCarrito")
                           ?.document(tvTitulo?.text.toString())
                           ?.set(articuloAnadir)?.addOnSuccessListener {
                               void: Void? ->  Toast.makeText(contexto, "Agregado al carrito de compras", Toast.LENGTH_LONG).show()
                           }

                }

            }
        }
    }

}