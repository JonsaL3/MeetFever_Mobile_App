package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.sexo.SexoWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceSexo {

    // busco una experiencia por cualquiera de sus campos
    fun getAllSexos(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/sexo/ObtenerTodosLosSexos"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val sexos = GsonBuilder()
                            .create()
                            .fromJson(any.toString(), SexoWrapper::class.java)
                        if (sexos.size > 0)
                            callback.callback(sexos)
                        else
                            callback.callback(0)
                    } else {
                        callback.callback(0)
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }

    }

}