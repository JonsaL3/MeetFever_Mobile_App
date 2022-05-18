package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalArgumentException

object WebServiceUsuario {

    // Iniciar Sesión
    fun inciarSesion(correo: String, contrasena: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/usuario/IniciarSesion"
        val jsonObject = JSONObject()

        try {
            // Preparo el request
            jsonObject.put("correo", correo)
            jsonObject.put("contrasena", contrasena)

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        when {
                            any.toString().contains(""""Tipo":"EMPRESA"""") -> {

                                val empresa = GsonBuilder()
                                    .create()
                                    .fromJson(any.toString(), Empresa::class.java)

                                callback.callback(empresa)

                            }
                            any.toString().contains(""""Tipo":"PERSONA"""") -> {

                                val persona = GsonBuilder()
                                    .create()
                                    .fromJson(any.toString(), Persona::class.java)

                                callback.callback(persona)
                            }
                            else -> callback.callback(0)
                        }

                    } else
                        callback.callback(0)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Registrarse
    fun registrarse(usuario: Usuario, context: Context, callback : WebServiceGenericInterface) {

        val url: String
//        val jsonObject = usuario.toJsonObject()
        val jsonObject = usuario.toJsonObject()

        if (usuario is Empresa) {
            "interface/api/meetfever/empresa/InsertarEmpresa".let {
                url = it
            }
        } else if (usuario is Persona) {
            "interface/api/meetfever/persona/InsertarPersona".let {
                url = it
            }
        } else throw IllegalArgumentException("El usuario no es de tipo Empresa o Persona")

        Log.d(":::", "REGISTRANDO -> ${usuario.toJsonObject()}")


        WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
            override fun callback(any: Any) {

                if (any.toString().isNotEmpty()) {
                    callback.callback(any)
                } else  {
                    callback.callback(0)
                }

            }
        })

    }

}