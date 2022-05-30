package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception

object WebServiceMeGusta {

    // si le he dado me gusta y le vuelvo a dar me lo quita y viceversa
    fun darMeGustaOQuitarlo(idOpinion: Int, idUsuario: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/MeGusta/DarMeGusta"
        val jsonObject = JSONObject().apply {
            put("Id_Opinion", idOpinion)
            put("Id_Usuario", idUsuario)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    Log.d(":::", any.toString())

                    if (any.toString().isNotEmpty()) {
                        callback.callback(any)
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