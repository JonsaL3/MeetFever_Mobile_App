package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderFacturaBinding
import es.indytek.meetfever.models.factura.Factura
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.FacturaViewHolder

class FacturaRecyclerViewAdapter (

    private val lista: List<Factura>,
    private val currentUsuario: Usuario

) : RecyclerView.Adapter<FacturaViewHolder>() {

    private lateinit var mHolder: FacturaViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val binding = ViewholderFacturaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FacturaViewHolder(binding.root, binding, currentUsuario)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val valor: Factura = lista[position]
        holder.bind(valor)
        mHolder = holder
    }

    override fun getItemCount(): Int = lista.size


}