package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.factura.FacturaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

object WebServiceFactura {

    fun obtenerFacturasDeUnUsuario(idUsuario: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerEntradasPorPersona"
        val jsonObject = JSONObject().apply {
            put("Id_Usuario", idUsuario)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val facturas = GsonBuilder()
                            .create()
                            .fromJson(any.toString(), FacturaWrapper::class.java)
                        if (facturas.size > 0)
                            callback.callback(facturas)
                        else
                            callback.callback(0)
                    } else
                        callback.callback(0)


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