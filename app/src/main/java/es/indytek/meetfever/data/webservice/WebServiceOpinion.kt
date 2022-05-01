package es.indytek.meetfever.data.webservice

import android.content.Context
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate

object WebServiceOpinion {

    // Obtener las 100 opiniones mas gustadas de las ultimas 24 horas
    fun find10PersonasQueQuizasConozca(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/Obtener100OpinionesMasGustadas24h"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), OpinionWrapper::class.java)
                        if (opiniones.size > 0)
                            callback.callback(opiniones)
                        else
                            callback.callback(0)
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}