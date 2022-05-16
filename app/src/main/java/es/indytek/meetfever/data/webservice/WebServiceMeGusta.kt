package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

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
                    Log.d(":::JSON", jsonObject.toString())
                    callback.callback("Petición de dar like o eliminarlo realizada correctamente.")
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}