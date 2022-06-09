package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.entrada.Entrada
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.experiencia.ExperienciaWrapper
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

        val url = "interface/api/meetfever/experiencia/ObtenerExperienciaGeneral"
        val jsonObject = JSONObject().apply {
            put("Palabra", busqueda)
        }

        Log.d(":::", jsonObject.toString())

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
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
            put("Titulo", nickname)
        }

        Log.d(":::", "jsonObject: $jsonObject")

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val empresa = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), ExperienciaWrapper::class.java)
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

    fun conseguirNumeroEntradas(experiencia: Experiencia, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ObtenerEntradasPorExperiencia"
        val jsonObject = JSONObject().apply {
            put("Id", experiencia.id)
        }


        Log.d(":::", jsonObject.toString())

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    Log.d(":::", "ABBBB -> " + any.toString())
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

    fun findExperienciaById(idExperiencia: Int,context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/experiencia/ObtenerExperienciaPorId"
        val jsonObject = JSONObject().apply {
            put("Id", idExperiencia)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val experiencia = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), Experiencia::class.java)
                        callback.callback(experiencia)
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