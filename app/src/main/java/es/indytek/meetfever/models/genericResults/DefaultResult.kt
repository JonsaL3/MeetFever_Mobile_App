package es.indytek.meetfever.models.genericResults

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DefaultResult(

    @SerializedName("JSON_OUT")
    var jSONOUT: String?,
    @SerializedName("MENSAJE")
    var mENSAJE: String,
    @SerializedName("RETCODE")
    var rETCODE: Int

) : Serializable {

}