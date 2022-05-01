package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

object WebServiceExperiencia {

    // Iniciar sesión a partir de usuario y contraseña
    fun findTop4ExperienciasMasComentadas(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ObtenerTop4ExperienciasMasOpinadas"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val experiencias = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), ExperienciaWrapper::class.java)
                        if (experiencias.size > 0)
                            callback.callback(experiencias)
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