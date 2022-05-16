package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceOpinion {

    // Obtener las 100 opiniones mas gustadas de las ultimas 24 horas
    fun find100OpinionesMasGustadas24h(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/ObtenerOpinionesMasGustadas24Horas"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), OpinionWrapper::class.java)
                        if (opiniones.size > 0)
                            callback.callback(opiniones)
                        else
                            callback.callback(0)
                    } else
                        callback.callback(0)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Obtener las 100 opiniones mas gustadas de las ultimas 24 horas
    fun obtenerOpinionPorIdAutor(autor: Usuario, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/ObtenerOpinionPorIdAutor"
        val jsonObject = JSONObject().put("Id_Autor", autor.id)

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), OpinionWrapper::class.java)
                        if (opiniones.size > 0)
                            callback.callback(opiniones)
                        else
                            callback.callback(0)
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