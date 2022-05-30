package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.persona.PersonaWrapper
import es.indytek.meetfever.models.typeAdapters.LocalDateTypeAdapter
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate

object WebServicePersona {

    // busco una persona por cualquiera de sus campos
    fun buscarPersonas(busqueda: String, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerPersonaGeneral"
        val jsonObject = JSONObject().apply {
            put("Palabra", busqueda)
        }

        try {

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val personas = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), PersonaWrapper::class.java)
                        if (personas.size > 0)
                            callback.callback(personas)
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

    // Obtengo las 10 personas con mas seguidores
    fun findTop10PersonasConMasSeguidores(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerTopPersonasConMasSeguidores"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val personas = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), PersonaWrapper::class.java)
                        if (personas.size > 0)
                            callback.callback(personas)
                        else
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

    // Obtengo las 10 personas con mas seguidores
    fun find10PersonasQueQuizasConozca(usuario: Usuario, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerPersonasQueQuizasConozca"
        val jsonObject = JSONObject()

        try {
            // preparo el request
            jsonObject.put("Id", usuario.id)

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val personas = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), PersonaWrapper::class.java)
                        if (personas.size > 0)
                            callback.callback(personas)
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

    // Obtengo todas las personas
    fun findAllPersonas(context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerTodasLasPersonas"
        val jsonObject = JSONObject()

        try {

            WebService.processRequestGet(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val personas = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), PersonaWrapper::class.java)
                        if (personas.size > 0)
                            callback.callback(personas)
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

    // Obtengo todas las personas relacionadas conmigo
    fun findAllRelatedPersonas(usuario: Usuario, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ObtenerTodasLasPersonasQueQuizasConozca"
        val jsonObject = JSONObject()

        try {

            jsonObject.put("Id", usuario.id)

            WebService.processRequestPost(context, url, jsonObject, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any.toString().isNotEmpty()) {
                        // Obtengo el response
                        val personas = GsonBuilder()
                            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
                            .create()
                            .fromJson(any.toString(), PersonaWrapper::class.java)
                        if (personas.size > 0)
                            callback.callback(personas)
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

    // actualizar el usuario
    fun actualizarPersona(persona: Persona, context: Context, callback : WebServiceGenericInterface) {

        val url = "interface/api/meetfever/persona/ActualizarPersona"
        val jsonObject = persona.toJsonObject()

        Log.d(":::PERSONAACTUALIZAR -> ", persona.sexo.toJsonObject().toString())

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