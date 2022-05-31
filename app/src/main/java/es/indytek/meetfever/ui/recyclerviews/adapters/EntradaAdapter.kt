package es.indytek.meetfever.ui.recyclerviews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.databinding.ViewholderEntradaBinding
import es.indytek.meetfever.models.entrada.Entrada
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.ui.recyclerviews.viewholders.EntradaViewHolder

class EntradaAdapter (

    private val lista: List<Entrada>,
    private val experiencia: Experiencia

) : RecyclerView.Adapter<EntradaViewHolder>() {

    private lateinit var mHolder: EntradaViewHolder
    private var mPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val binding = ViewholderEntradaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntradaViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {
        val valor: Entrada = lista[position]
        holder.bind(valor, experiencia, position)

        mHolder = holder

    }

    override fun getItemCount(): Int = lista.size


}