package es.indytek.meetfever.models.typeAdapters

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.LocalDateTime

class LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {

    override fun write(out: JsonWriter?, value: LocalDateTime?) {
        out?.value(value.toString())
    }

    override fun read(into: JsonReader?): LocalDateTime {
        return LocalDateTime.parse(into?.nextString())
    }

}