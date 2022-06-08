package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderTitularBinding
import es.indytek.meetfever.models.factura.Titular
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.viewholders.TitularesViewHolder

class TitularesRecyclerViewAdapter (

    private val lista: List<Titular>,
    private val currentUsuario: Usuario

) : RecyclerView.Adapter<TitularesViewHolder>() {

    private lateinit var mHolder: TitularesViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitularesViewHolder {
        val binding = ViewholderTitularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TitularesViewHolder(binding.root, binding, currentUsuario)
    }

    override fun onBindViewHolder(holder: TitularesViewHolder, position: Int) {
        val valor: Titular = lista[position]
        holder.bind(valor)
        mHolder = holder
    }

    override fun getItemCount(): Int = lista.size


}