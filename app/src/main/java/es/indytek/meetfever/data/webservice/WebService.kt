package es.indytek.meetfever.data.webservice

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import es.indytek.meetfever.models.genericResults.DefaultResult
import org.json.JSONObject

object WebService {

    fun processRequestPost(context: Context, url: String, json: JSONObject?, callback: WebServiceGenericInterface) {

        val url = "https://meetfever.eu/$url"

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, json,
                { response ->

                    val resGson: DefaultResult = Gson().fromJson(response.getJSONObject("data").toString(), DefaultResult::class.java)

                    Log.w("prueba", resGson.toString())

                    if (resGson.rETCODE == 0) {
                        resGson.jSONOUT?.let { callback.callback(it) }
                    }

                    else {
                        // TODO
                    }
                },
                { error ->
                    Log.w(":::, ", "Parece que Gonzalo no sabe programar")
                    //TODO ERROR
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun processRequestGet(context: Context, url: String, json: JSONObject?, callback: WebServiceGenericInterface) {

        val url = "https://meetfever.eu/$url"

        try {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, json,
                { response ->

                    val resGson: DefaultResult = Gson().fromJson(response.getJSONObject("data").toString(), DefaultResult::class.java)

                    Log.w("prueba", resGson.toString())

                    if (resGson.rETCODE == 0) {
                        resGson.jSONOUT?.let { callback.callback(it) }
                    }

                    else {
                        // TODO
                    }
                },
                { error ->
                    Log.w(":::, ", "Parece que Gonzalo no sabe  pero GET")
                    //TODO ERROR
                }
            )

            val queue = Volley.newRequestQueue(context)
            queue.add(jsonObjectRequest)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}