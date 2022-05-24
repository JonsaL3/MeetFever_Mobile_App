package es.indytek.meetfever.utils

import android.graphics.Bitmap
import android.util.Base64
import android.util.Base64OutputStream
import java.io.ByteArrayOutputStream
import java.io.File

object Extensions {

    // convierte un file a base 664
    fun convertImageFileToBase64(imageFile: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
                imageFile.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return outputStream.toString()
        }
    }

    // convierte un bitmap a base 64 KOMPRESSOR
    fun Bitmap.toBase64String(): String {
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,5,this)
            return java.util.Base64.getEncoder().encodeToString(toByteArray())
        }
    }

}