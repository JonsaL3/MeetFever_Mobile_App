package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

object WebServiceOpinion {

    // busco una opinion por cualquiera de sus campos
    fun buscarOpinion(busqueda: String, idUsuario: Int,           context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/ObetenerOpinionGeneral"
        val jsonObject = JSONObject().apply {
            put("Palabra", busqueda)
            put("Id_Usuario", idUsuario)
        }

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
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }

    }

    // Obtener las 100 opiniones mas gustadas de las ultimas 24 horas
    fun find100OpinionesMasGustadas24h(idUsuario: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/ObtenerOpinionesMasGustadas24Horas"
        val jsonObject = JSONObject().apply {
            put("Id_Usuario", idUsuario)
        }

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

    // Obtener las 100 opiniones mas gustadas de las ultimas 24 horas
    fun obtenerOpinionPorIdAutor(autor: Usuario, usuario: Usuario, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/ObtenerOpinionPorIdAutor"
        val jsonObject = JSONObject().apply {
            put("Id_Autor", autor.id)
            put("Id_Usuario", usuario.id)
        }

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
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }
    }

    fun insertarOpinion(
        descripcion: String,
        fecha: LocalDateTime,
        idEmoticono: Int,
        idAutor: Int,
        idEmpresa: Int?,
        idExperiencia: Int?,
        context: Context,
        callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/opinion/InsertarOpinion"
        val jsonObject = JSONObject().apply {
            put("Titulo", "")
            put("Descripcion", descripcion)
            put("Fecha", fecha.toString())
            put("Id_Emoticono", idEmoticono)
            put("Id_Autor", idAutor)
            put("Id_Empresa", idEmpresa)
            put("Id_Experiencia", idExperiencia)
        }

        Log.w(":::", jsonObject.toString())

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {

                        if (any.toString().contains("true")) {
                            callback.callback(any)
                        } else {
                            callback.callback(0)
                        }

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