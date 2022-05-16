package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.empresa.EmpresaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime

object WebServiceEmpresa {

    // Encontrar empresa por id
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
        }
    }

}