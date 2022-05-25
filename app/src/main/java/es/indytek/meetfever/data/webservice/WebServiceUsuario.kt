package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.mesigue.MeSigue
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.sexo.Sexo
import es.indytek.meetfever.models.typeAdapters.LocalDateTimeTypeAdapter
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.models.usuario.UsuarioWrapper
import org.json.JSONObject
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

object WebServiceUsuario {

    // pregunto si ya sigo a un usuario
    fun isSeguidoPorUser(idSeguidor: Int, idSeguido: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/usuario/IsFollow"
        val jsonObject = JSONObject().apply {
            put("Seguidor", idSeguidor)
            put("Seguido", idSeguido)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {

                        val isFollowed = GsonBuilder()
                            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
                            .create()
                            .fromJson(any.toString(), MeSigue::class.java)

                        callback.callback(isFollowed)

                    } else {
                        callback.callback(0)
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // seguir o dejar de seguir SeguirAUnUsuario
    fun seguirDejarDeSeguir(idSeguidor: Int, idSeguido: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/usuario/SeguirAUnUsuario"
        val jsonObject = JSONObject().apply {
            put("Seguidor", idSeguidor)
            put("Seguido", idSeguido)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        callback.callback(any)
                    } else {
                        callback.callback(0)
                    }

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

                    Log.d(":::", any.toString())

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
                                    .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
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

        when (usuario) {

            is Empresa -> {
                "interface/api/meetfever/empresa/InsertarEmpresa".let {
                    url = it
                }
            }
            is Persona -> {
                "interface/api/meetfever/persona/InsertarPersona".let {
                    url = it
                }
            }
            else -> throw IllegalArgumentException("El usuario no es de tipo Empresa o Persona")
        }

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

    // Obtengo los seguidores y los seguidos
    fun obtenerSeguidores(idUsuario: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/usuario/ObtenerSeguidores"
        val jsonObject = JSONObject()

        try {
            // Preparo el request
            jsonObject.put("Id", idUsuario)

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val seguidores = GsonBuilder()
                            .create()
                            .fromJson(any.toString(), UsuarioWrapper::class.java)

                        callback.callback(seguidores)

                    } else
                        callback.callback(0)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun obtenerSeguidos(idUsuario: Int, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/usuario/ObtenerSeguidos"
        val jsonObject = JSONObject()

        try {
            // Preparo el request
            jsonObject.put("Id", idUsuario)

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val seguidos = GsonBuilder()
                            .create()
                            .fromJson(any.toString(), UsuarioWrapper::class.java)

                        callback.callback(seguidos)

                    } else
                        callback.callback(0)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}