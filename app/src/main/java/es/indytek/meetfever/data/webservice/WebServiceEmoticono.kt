package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

object WebServiceEmoticono {

    // Descargo los emoticonos que puedo usar en la app desde la base de datos
    // Iniciar sesión a partir de usuario y contraseña
    fun obtenerTodosLosEmoticonos(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/emoticono/ObtenerTodosLosEmoticonos"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val emoticonos = GsonBuilder()
                            .create()
                            .fromJson(any.toString(), EmoticonoWrapper::class.java)
                        if (emoticonos.size > 0)
                            callback.callback(emoticonos)
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

}