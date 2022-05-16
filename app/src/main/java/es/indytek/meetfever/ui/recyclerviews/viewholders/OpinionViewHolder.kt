package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceMeGusta
import es.indytek.meetfever.databinding.ViewholderOpinionBinding
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Utils

class OpinionViewHolder(

    private val view: View,
    private val binding: ViewholderOpinionBinding,
    private val claseOrigen: Class<*>,

) : RecyclerView.ViewHolder(view) {

    @SuppressLint("ResourceAsColor")
    fun bind(objeto: Opinion) {

        binding.textoNombre.text = objeto.autor.nick
        binding.opinionText.text = objeto.descripcion

        // lo pinto de un color u otro en función de donde vengo
        if (claseOrigen.simpleName == TrendingsFragment::class.java.simpleName) {
            binding.opinionContainer.backgroundTintList = view.context.getColorStateList(R.color.naranja_opinion)
        } else if (claseOrigen.simpleName == PerfilFragment::class.java.simpleName) {
            val foto = objeto.autor.fotoPerfil
            foto?.let { // si tengo foto obtengo su color predominante

                val color = Utils.getDominantColorInImageFromBase64(it)

                if (color == Color.BLACK) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                    binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                } else {
                    binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(color)
                }

            }?: kotlin.run { // si no tiene foto lo pongo de blanco
                binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            }
        }

        // planto la foto de perfil o su placeholder
        val foto = objeto.autor.fotoPerfil
        foto?.let {
            Utils.putBase64ImageIntoImageViewWithPlaceholder(binding.imagenAutor, it, itemView.context, R.drawable.ic_default_user_image)
        }?: kotlin.run {
            Utils.putResourceImageIntoImageView(binding.imagenAutor, R.drawable.ic_default_experience, itemView.context)
        }

        // planto el emoji de la opinion
        val emoji = objeto.eMOTICONO.emoji
        emoji.let {
            try {
                Utils.putBase64ImageIntoImageView(binding.emojiOpinion, it, itemView.context)
            } catch (e: IllegalArgumentException) {
                Log.e(":::", "Parece que alguien se dejo el emoji de prueba puesto en la opinión")
            }
        }

        // TODO compruebo si el usuario le dió me gusta o no para pintar un corazon u otro

        // TODO pinto el numero de megustas de la opinion

        // Creo un listener que me permita darle a me gusta a una opinion
        binding.botonMeGusta.setOnClickListener {

            Log.d(":::", "AUTOR -> ${objeto.autor.id}")
            Log.d(":::", "OPINION -> ${objeto.id}")

            WebServiceMeGusta.darMeGustaOQuitarlo(objeto.id, objeto.autor.id, view.context, object: WebServiceGenericInterface {

                @RequiresApi(Build.VERSION_CODES.Q)
                override fun callback(any: Any) {

                    // TODO esto ha de ejecutarse una vez sepa si le he dado a me gusta o no

                }
            })

        }

    }

}