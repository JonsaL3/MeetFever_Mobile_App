package es.indytek.meetfever.data.webservice

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import es.indytek.meetfever.R
import es.indytek.meetfever.models.genericResults.DefaultResult
import es.indytek.meetfever.ui.activities.LoginActivity
import es.indytek.meetfever.utils.DialogAcceptCustomActionInterface
import es.indytek.meetfever.utils.DialogMaker
import es.indytek.meetfever.utils.Utils
import org.json.JSONObject

object WebService {

    fun processRequestPost(context: Context, url: String, json: JSONObject?, callback: WebServiceGenericInterface) {

        if (!Utils.tengoInternet(context)) {
            DialogMaker(context, context.getString(R.string.error), context.getString(R.string.no_hay_internet)).warningAcceptCustomAction(context.getString(R.string.volver_al_login), context.getString(
                            R.string.esperar), object: DialogAcceptCustomActionInterface {
                override fun acceptButton() {
                    // vuelvo a la login activity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as AppCompatActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
            return
        }

        val url = "https://meetfever.eu/$url"

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, json,
                { response ->

                    val resGson: DefaultResult = Gson().fromJson(response.getJSONObject("data").toString(), DefaultResult::class.java)

                    Log.w("prueba", resGson.toString())

                    if (resGson.rETCODE == 0) {
                        resGson.jSONOUT?.let {
                            callback.callback(it)
                        }
                    } else {
                        callback.callback("")
                        Log.d(":::", "RETCODE DISTINTO DE 0 " + resGson.rETCODE + " " + resGson.mENSAJE + " " + resGson.jSONOUT)
                    }
                },
                { error ->
                    Log.w(":::, ", "Parece que Gonzalo no sabe programar POST -> $error")
                    DialogMaker(context, context.getString(R.string.error), context.getString(R.string.error_post)).warningNoCustomActions()
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }

    }

    fun processRequestGet(context: Context, url: String, json: JSONObject?, callback: WebServiceGenericInterface) {

        if (!Utils.tengoInternet(context)) {
            DialogMaker(context, context.getString(R.string.error), context.getString(R.string.no_hay_internet)).warningAcceptCustomAction(context.getString(R.string.volver_al_login), context.getString(
                R.string.esperar), object: DialogAcceptCustomActionInterface {
                override fun acceptButton() {
                    // vuelvo a la login activity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as AppCompatActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
            return
        }

        val url = "https://meetfever.eu/$url"

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, json,
                { response ->

                    val resGson: DefaultResult = Gson().fromJson(response.getJSONObject("data").toString(), DefaultResult::class.java)

                    //Log.w(":::PRUEBA", resGson.toString())

                    if (resGson.rETCODE == 0) {
                        resGson.jSONOUT?.let { callback.callback(it) }
                    } else {
                        // TODO CARTEL ERROR
                        callback.callback("")
                        Log.d(":::", "RETCODE DISTINTO DE 0 " + resGson.rETCODE + " " + resGson.mENSAJE + " " + resGson.jSONOUT)
                    }
                },
                { error ->
                    Log.w(":::, ", "Parece que Gonzalo no sabe pero GET -> $error")
                    DialogMaker(context, context.getString(R.string.error), context.getString(R.string.error_get)).warningNoCustomActions()
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }

    }

    fun processRequestPut(context: Context, url: String, json: JSONObject?, callback: WebServiceGenericInterface) {

        if (!Utils.tengoInternet(context)) {
            DialogMaker(context, context.getString(R.string.error), context.getString(R.string.no_hay_internet)).warningAcceptCustomAction(context.getString(R.string.volver_al_login), context.getString(
                R.string.esperar), object: DialogAcceptCustomActionInterface {
                override fun acceptButton() {
                    // vuelvo a la login activity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    (context as AppCompatActivity).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
            return
        }

        val url = "https://meetfever.eu/$url"

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.PUT, url, json,
                { response ->

                    val resGson: DefaultResult = Gson().fromJson(response.getJSONObject("data").toString(), DefaultResult::class.java)

                    Log.w("prueba", resGson.toString())

                    if (resGson.rETCODE == 0) {
                        resGson.jSONOUT?.let {
                            callback.callback(it)
                        }
                    } else {
                        // TODO
                        callback.callback("")
                        Log.d(":::", "RETCODE DISTINTO DE 0 " + resGson.rETCODE + " " + resGson.mENSAJE + " " + resGson.jSONOUT)
                    }
                },
                { error ->
                    Log.w(":::, ", "Parece que Gonzalo no sabe programar POST -> $error")
                    DialogMaker(context, context.getString(R.string.error), context.getString(R.string.error_put)).warningNoCustomActions()
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.enviarRegistroDeErrorABBDD(
                context = context,
                stacktrace = e.message.toString(),
            )
        }

    }

}