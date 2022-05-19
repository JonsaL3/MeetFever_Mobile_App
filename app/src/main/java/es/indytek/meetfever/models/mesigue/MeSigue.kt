package es.indytek.meetfever.models.mesigue


import com.google.gson.annotations.SerializedName

data class MeSigue(
    @SerializedName("Mesigue")
    var mesigue: Boolean = false
)