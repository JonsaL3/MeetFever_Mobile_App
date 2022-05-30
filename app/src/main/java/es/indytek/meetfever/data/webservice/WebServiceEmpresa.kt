package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceEmpresa {

    // busco una experiencia por cualquiera de sus campos
    fun buscarEmpresa(busqueda: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ObtenerEmpresaGeneral"
        val jsonObject = JSONObject().apply {
            put("Palabra", busqueda)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val opiniones = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), EmpresaWrapper::class.java)
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

    // Encontrar empresa por nick
    fun findEmpresaByNickname(nickname: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ObtenerEmpresaPorNick"
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

    // Iniciar sesión a partir de usuario y contraseña
    fun findTop10EmpresasConMasSeguidores(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ObtenerTopEmpresasConMasSeguidores"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val empresas = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), EmpresaWrapper::class.java)
                        if (empresas.size > 0)
                            callback.callback(empresas)
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

    // Obtener todas las empresas
    fun findAllEmpresas(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ObtenerTodasLasEmpresas"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val empresas = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), EmpresaWrapper::class.java)
                        if (empresas.size > 0)
                            callback.callback(empresas)
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

    // Obtener todas las empresas
    fun actualizarEmpresa(empresa: Empresa, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/empresa/ActualizarEmpresa"
        val jsonObject = empresa.toJsonObject()

        Log.d(":::EMPRESAACTUALIZAR -> ", jsonObject.toString())

        try {

            WebService.processRequestPut(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        callback.callback(any)
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