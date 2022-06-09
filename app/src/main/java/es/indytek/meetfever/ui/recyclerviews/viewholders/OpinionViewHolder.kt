package es.indytek.meetfever.ui.recyclerviews.viewholders

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEmpresa
import es.indytek.meetfever.data.webservice.WebServiceExperiencia
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceMeGusta
import es.indytek.meetfever.databinding.ViewholderOpinionBinding
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.opinion.Opinion
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.fragments.mainfragments.TrendingsFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.perfil.PerfilFragment
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Utils
import java.time.format.DateTimeFormatter


class OpinionViewHolder(

    private val view: View,
    private val binding: ViewholderOpinionBinding,
    private val claseOrigen: Class<*>,
    private val currentUsuario: Usuario

) : RecyclerView.ViewHolder(view) {

    @SuppressLint("ResourceAsColor")
    fun bind(objeto: Opinion) {

        binding.textoNombre.text = objeto.autor.nick
        binding.opinionText.text = objeto.descripcion

        if (objeto.idEmpresa != 0) {

            WebServiceEmpresa.findEmpresaById(objeto.idEmpresa, view.context, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        binding.empresaOpinion.text = "Error"
                    } else {
                        val empresa = any as Empresa
                        binding.empresaOpinion.text = empresa.nick
                        Animations.mostrarVistaSuavemente(binding.empresaViewer)
                    }

                }
            })

        }

        if (objeto.idExperiencia != 0) {

            WebServiceExperiencia.findExperienciaById(objeto.idExperiencia, view.context, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) {
                        binding.experienciaOpinion.text = "Error"
                    } else {
                        val experiencia = any as Experiencia
                        binding.experienciaOpinion.text = experiencia.titulo
                        Animations.mostrarVistaSuavemente(binding.experienciaViewer)
                    }

                }
            })

        }

        binding.experienciaOpinion.text = objeto.idExperiencia.toString()

        if (Utils.isFamous(objeto.autor))
            Animations.mostrarVistaSuavemente(binding.famousRectangle)

        // lo pinto de un color u otro en función de donde vengo
        if (claseOrigen.simpleName == TrendingsFragment::class.java.simpleName) {
            binding.opinionContainer.backgroundTintList = view.context.getColorStateList(R.color.naranja_opinion)
        } else if (claseOrigen.simpleName == PerfilFragment::class.java.simpleName) {
            val foto = objeto.autor.fotoPerfil
            foto?.let { // si tengo foto obtengo su color predominante

                val color = Utils.getDominantColorInImageFromBase64(it)

                if (Utils.colorIsConsideredDark(color)) { // puede ocurrir que el base 64 este mal formado, por lo que devolverá negro y arruinará la targeta
                    binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                } else {
                    binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(color)
                }

            }?: kotlin.run { // si no tiene foto lo pongo de blanco
                binding.opinionContainer.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
            }
        }

        // planto la foto de perfil o su placeholder
        Utils.pintarFotoDePerfil(objeto.autor, binding.imagenAutor, view.context)

        // planto el emoji de la opinion
        val emoji = objeto.eMOTICONO.emoji
        emoji.let {
            try {
                Utils.putBase64ImageIntoImageView(binding.emojiOpinion, it, itemView.context)
            } catch (e: IllegalArgumentException) {
                Log.e(":::", "Parece que alguien se dejo el emoji de prueba puesto en la opinión")
                Utils.enviarRegistroDeErrorABBDD(
                    context = view.context,
                    stacktrace = e.message.toString(),
                )
            }
        }

        // pinto el numero de me gustas
        binding.numeroMeGusta.text = objeto.numeroLikes.toString()

        // pinto el me gusta en caso de que el usuario le haya dado like
        if (objeto.like) {

            Utils.putResourceImageIntoImageViewWithoutCorners(binding.btnMeGusta, R.drawable.ic_likedbutton, itemView.context)
        } else {

            Utils.putResourceImageIntoImageViewWithoutCorners(binding.btnMeGusta, R.drawable.ic_likebutton, itemView.context)
        }

        binding.fechaOpinion.text = objeto.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).toString()

        // Creo un listener que me permita darle a me gusta a una opinion
        binding.btnMeGusta.setOnClickListener {

            WebServiceMeGusta.darMeGustaOQuitarlo(objeto.id, currentUsuario.id, view.context, object: WebServiceGenericInterface {

                @RequiresApi(Build.VERSION_CODES.Q)
                override fun callback(any: Any) {

                    if (any == 0) {
                        Toast.makeText(view.context, "Error al realizar un me gusta.", Toast.LENGTH_SHORT).show()
                    } else {

                        if (!objeto.like) {
                            objeto.numeroLikes += 1
                            objeto.like = true
                            Utils.putResourceImageIntoImageViewWithoutCorners(binding.btnMeGusta, R.drawable.ic_likedbutton, itemView.context)
                            binding.numeroMeGusta.text = objeto.numeroLikes.toString()
                        } else {
                            objeto.numeroLikes -= 1
                            objeto.like = false
                            Utils.putResourceImageIntoImageViewWithoutCorners(binding.btnMeGusta, R.drawable.ic_likebutton, itemView.context)
                            binding.numeroMeGusta.text = objeto.numeroLikes.toString()
                        }

                    }

                }
            })

        }

    }

}