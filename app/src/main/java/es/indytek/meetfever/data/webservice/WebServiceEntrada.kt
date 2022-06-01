package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.entrada.Entrada
import es.indytek.meetfever.models.mesigue.MeSigue
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.Sexo
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceEntrada {

    // pregunto si ya sigo a un usuario
    fun insertarCompra(entrada: Entrada, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ComprarExperiencia"
        val entradaJSON = JSONObject(

            GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                .create()
                .toJson(entrada, Entrada::class.java)
        )


        Log.d(":::", entradaJSON.toString())

        try {

            WebService.processRequestPost(context, url, entradaJSON, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {

                        callback.callback("")

                    } else {
                        callback.callback(0)
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}