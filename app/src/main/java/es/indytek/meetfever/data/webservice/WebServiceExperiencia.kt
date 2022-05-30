package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
import es.indytek.meetfever.models.opinion.OpinionWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceExperiencia {

    // busco una experiencia por cualquiera de sus campos
    fun buscarExperiencia(busqueda: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/BUSQUEDAEXPERIENCIA" // TODO URL DE ALBERTO
        val jsonObject = JSONObject().apply {
            put("busqueda", busqueda)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), ExperienciaWrapper::class.java)
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

    // Iniciar sesión a partir de usuario y contraseña
    fun findTop4ExperienciasMasComentadas(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ObtenerTop4ExperienciasMasOpinadas"
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

    // Iniciar sesión a partir de usuario y contraseña
    fun findAllExperiencias(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ObtenerTodasLasExperiencias"
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

    // Encontrar experiencia por titulo
    fun findExperienciaByTitulo(nickname: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ObtenerExperienciasPorTitulo"
        val jsonObject = JSONObject().apply {
            put("Nick", nickname)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val empresa = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), Empresa::class.java)
                        callback.callback(empresa)
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