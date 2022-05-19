package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceOpinion
import es.indytek.meetfever.databinding.ViewholderEmpresaBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Utils

class EmpresaViewHolder(

    private val view: View,
    private val binding: ViewholderEmpresaBinding,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    fun bind(objeto: Empresa) {

        // pongo el texto
        binding.texto.text = objeto.nombreEmpresa.toString()

        // Si tiene foto de perfil, la pinto
        Utils.pintarFotoDePerfil(objeto, binding.imagen, view.context)

        // pinto el degradado del color que corresponda
        val foto = objeto.fotoPerfil
        foto?.let { // si tengo foto obtengo su color predominante
            val color = Utils.getDominantColorInImageFromBase64(it)
            binding.degradado.backgroundTintList = ColorStateList.valueOf(color)
        }?: kotlin.run { // si no tiene foto lo pongo de blanco
            binding.degradado.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
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
        WebServiceOpinion.obtenerOpinionPorIdAutor(empresa, currentUsuario, itemView.context, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any == 0) {
                    opiniones = OpinionWrapper()
                } else {
                    opiniones = any as OpinionWrapper
                }

                val fragmento = PerfilFragment.newInstance(empresa, currentUsuario)
                activity.supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmento).commit()
            }
        })

    }

}