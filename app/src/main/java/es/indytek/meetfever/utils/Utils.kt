package es.indytek.meetfever.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import es.indytek.meetfever.R
import java.io.ByteArrayInputStream

object Utils {

    // Compruebo que tengo una conexion a internet
    fun tengoInternet(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i(":::", "InternetConnectionTester.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i(":::", "InternetConnectionTester.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i(":::", "InternetConnectionTester.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }

        return false
    }

    fun putBase64ImageIntoImageView(imageView: ImageView, base64Image: String, context: Context) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(55))

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .apply(requestOptions)
            .into(imageView)

    }

    fun putBase64ImageIntoImageViewWithPlaceholder(imageView: ImageView, base64Image: String, context: Context, placeholder: Int) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(55))

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .apply(requestOptions)
            .placeholder(placeholder)
            .into(imageView)
    }

    fun putBase64ImageIntoImageViewWithoutCorners(imageView: ImageView, base64Image: String, context: Context) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop())

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .apply(requestOptions)
            .into(imageView)

    }

    fun putBase64ImageIntoImageViewCircular(imageView: ImageView, base64Image: String, context: Context) {

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(500))

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .apply(requestOptions)
            .into(imageView)

    }

    fun getDominantColorInImage(image: Bitmap): Int {
        val swatch = Palette.from(image).generate()
        return swatch.getDominantColor(Color.BLACK)
    }

    fun getDominantColorInImageFromBase64(base64Image: String): Int {

        val image = BitmapFactory.decodeStream(ByteArrayInputStream(Base64.decode(base64Image, Base64.DEFAULT)))

        image?.let {
            return getDominantColorInImage(image)
        }?: kotlin.run {
            return Color.BLACK
        }

    }

    fun getDominantColorFromResourceImage(resourceImage: Int, context: Context): Int {
        val image = BitmapFactory.decodeResource(context.resources, resourceImage)
        return getDominantColorInImage(image)
    }

}