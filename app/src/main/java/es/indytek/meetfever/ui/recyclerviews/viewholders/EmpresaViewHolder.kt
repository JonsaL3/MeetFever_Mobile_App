package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.ViewholderEmpresaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Utils

class EmpresaViewHolder(

    private val view: View,
    private val binding: ViewholderEmpresaBinding

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Empresa) {
        // pongo el texto
        binding.texto.text = objeto.nombreEmpresa.toString()

        // Si tiene foto de perfil, la pinto
        val foto = objeto.fotoPerfil
        foto?.let {
            Utils.putBase64ImageIntoImageView(binding.imagen, it, itemView.context)
        }

        // en caso de click cargo el perfil
        binding.viewholderEmpresa.setOnClickListener {
            onClick(objeto)
        }

    }

    private fun onClick(empresa: Empresa) {

        val activity = view.context as AppCompatActivity
        var opiniones: OpinionWrapper

        // pido la opinion
        WebServiceOpinion.obtenerOpinionPorIdAutor(empresa, itemView.context, object:
            WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    opiniones = OpinionWrapper()
                } else {
                    opiniones = any as OpinionWrapper
                }

                val fragmento = PerfilFragment.newInstance(empresa, opiniones)
                activity.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
            }
        })

    }

}