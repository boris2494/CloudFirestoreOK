package com.example.borisvillalta.cloudfirestoreok.Adaptadores

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.borisvillalta.cloudfirestoreok.Clases.ArticuloCarritoCompra
import com.example.borisvillalta.cloudfirestoreok.Interfaz.InterfazDelete
import com.example.borisvillalta.cloudfirestoreok.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso



class AdaptadorCompras (var articulosCompra:ArrayList<ArticuloCarritoCompra>, var delete: InterfazDelete): RecyclerView.Adapter<AdaptadorCompras.viewHolderCompras>() {

    var items:ArrayList<ArticuloCarritoCompra>? = null

    init {
        this.items = articulosCompra
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderCompras {
        var vista = LayoutInflater.from(parent.context).inflate(R.layout.template_compras, parent, false)
        var viewHolder = viewHolderCompras(vista, delete, items!!)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: viewHolderCompras, position: Int) {
        var item = items?.get(position)

        holder.tvTitulo?.text = item?.nombre
        holder.tvPrecio?.text = item?.precio
        holder.tvCantidad?.text = item?.cantidad
        Picasso.get().load(item?.imagen).into(holder.ivImagen)

        holder.clickEliminar(holder.adapterPosition)
    }

    class viewHolderCompras(vista: View, delete: InterfazDelete, arregloArticulo: ArrayList<ArticuloCarritoCompra>): RecyclerView.ViewHolder(vista), View.OnClickListener {

        var contexto = vista.context
        var cloudFirebase = FirebaseFirestore.getInstance()
        var auth = FirebaseAuth.getInstance()
        var delete: InterfazDelete? = null
        var arregloArticulo: ArrayList<ArticuloCarritoCompra>

        var tvTitulo: TextView? = null
        var tvCantidad: TextView? = null
        var tvPrecio: TextView? = null
        var ivImagen: ImageView? = null
        var bEliminar: Button? = null

        init {
            this.tvTitulo = vista.findViewById(R.id.tvTituloCompras)
            this.tvCantidad = vista.findViewById(R.id.tvCantidadCompras)
            this.tvPrecio = vista.findViewById(R.id.tvPrecioCompras)
            this.ivImagen = vista.findViewById(R.id.ivImagenCompras)
            this.bEliminar = vista.findViewById(R.id.bEliminar)
            this.arregloArticulo = arregloArticulo
            this.delete = delete
        }

        fun clickEliminar(posicion: Int){
            bEliminar?.setOnClickListener(this)
        }

        //Click en eliminar, el contexto es en base al viewHolder
        override fun onClick(v: View?) {
            when(v?.id){
                R.id.bEliminar -> {
                    cloudFirebase.collection("usuarios").document(auth.currentUser!!.uid)
                            .collection("agregadoAlCarrito").document(tvTitulo?.text.toString())
                            .delete().addOnCompleteListener(OnCompleteListener<Void>{
                                task: Task<Void> ->  Toast.makeText(contexto, "Articulo eliminado", Toast.LENGTH_LONG).show()
                                //Se envia el index a traves de la interfaz
                                this.delete?.articuloEliminado(adapterPosition)

                            })

                }

            }
        }

    }
}