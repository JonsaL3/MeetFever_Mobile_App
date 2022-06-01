package es.indytek.meetfever.data.webservice

import android.content.Context
import org.json.JSONObject
import java.lang.Exception

object WebServiceRegistroErrores {

    // Enviar error a la bbdd
    fun enviarError(stacktrace: String, appFuente: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/registroerrorcontroler/InsertarRegistroDeError"
        val jsonObject = JSONObject().apply {
            put("Excepcion", stacktrace)
            put("Aplicacion_Fuente", appFuente)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        callback.callback(any)
                    } else
                        callback.callback(0)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
//            Utils.enviarRegistroDeErrorABBDD(
//                context = context,
//                stacktrace = e.message.toString(),
//            )
        }
    }

}