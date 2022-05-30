package es.indytek.meetfever.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import es.indytek.meetfever.R
import es.indytek.meetfever.models.empresa.Empresa
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.ui.recyclerviews.adapters.ExperienciaRecyclerViewAdapter
import io.github.yavski.fabspeeddial.FabSpeedDial
import nl.joery.animatedbottombar.AnimatedBottomBar
import java.io.ByteArrayInputStream
import java.lang.IllegalStateException

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

    fun putBase64ImageIntoImageViewWithoutCornersWithPlaceholder(imageView: ImageView, base64Image: String, context: Context, placeholder: Int) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop())

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .placeholder(placeholder)
            .apply(requestOptions)
            .into(imageView)

    }

    fun putBase64ImageIntoImageViewCircularWithPlaceholder(imageView: ImageView, base64Image: String, context: Context, placeholder: Int) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(500))

        Glide.with(context)
            .load(Base64.decode(base64Image, Base64.DEFAULT))
            .apply(requestOptions)
            .placeholder(placeholder)
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

    fun putResourceImageIntoImageView(imageView: ImageView, resourceImage: Int, context: Context) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(50))
        Glide.with(context)
            .load(resourceImage)
            .apply(requestOptions)
            .into(imageView)
    }

    fun putResourceImageIntoImageViewCircular(imageView: ImageView, resourceImage: Int, context: Context) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(500))
        Glide.with(context)
            .load(resourceImage)
            .apply(requestOptions)
            .into(imageView)
    }

    fun putResourceImageIntoImageViewWithoutCorners(imageView: ImageView, resourceImage: Int, context: Context) {
        var requestOptions = RequestOptions()
        Glide.with(context)
            .load(resourceImage)
            .into(imageView)
    }

    fun getColorWithAlpha(color: Int, alpha: Float): Int {
        val a = Math.round(Color.alpha(color) * alpha)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.argb(a, r, g, b)
    }

    fun setTextColorAsResource(textView: TextView, color: Int, context: Context) {
        textView.setTextColor(ContextCompat.getColor(context, color))
    }

    fun pintarFotoDePerfil(usuario: Usuario, imageView: ImageView, context: Context) {

        val foto = usuario.fotoPerfil

        foto?.let { // puede existir la foto pero no ser correcta

            if (usuario is Empresa) {
                putBase64ImageIntoImageViewWithPlaceholder(imageView, it, context, R.drawable.ic_default_enterprise_black_and_white)
            } else {
                // por ejemplo en las opiniones no se si es una empresa o una persona, asique por defecto que la pinte circular ahi siempre
                putBase64ImageIntoImageViewCircularWithPlaceholder(imageView, it, context, R.drawable.ic_default_user_image)
            }

        }?: kotlin.run { // si no tiene foto porque es null...

            if (usuario is Empresa) {
                putResourceImageIntoImageViewWithoutCorners(imageView, R.drawable.ic_default_enterprise_black_and_white, context)
            } else {
                putResourceImageIntoImageViewCircular(imageView, R.drawable.ic_default_user_image, context)
            }

        }

    }

    fun pintarFotoDePerfilDirectamente(usuario: Usuario, nuevaFoto: String, imageView: ImageView, context: Context) {

        if (usuario is Empresa) {
            putBase64ImageIntoImageViewWithPlaceholder(imageView, nuevaFoto, context, R.drawable.ic_default_enterprise_black_and_white)
        } else {
            // por ejemplo en las opiniones no se si es una empresa o una persona, asique por defecto que la pinte circular ahi siempre
            putBase64ImageIntoImageViewCircularWithPlaceholder(imageView, nuevaFoto, context, R.drawable.ic_default_user_image)
        }

    }

    fun cambiarDeFragmentoGuardandoElAnterior(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        tag: String,
        container: Int
    ) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(container, fragment, tag)
        fragmentTransaction.addToBackStack(tag)
        fragmentTransaction.commit()
    }

    fun ocultarElementosUI(activity: Activity) {
        ocultarBotonAjustes(activity)
        ocultarBottomBar(activity)
        ocultarFabSpeedDial(activity)
        ocultarBotonMenu(activity)
        ocultarTextoBienvenida(activity)
    }
    fun mostrarElementosUI(activity: Activity) {
        mostrarBotonAjustes(activity)
        mostrarBottomBar(activity)
        mostrarFabSpeedDial(activity)
        mostrarBotonMenu(activity)
        mostrarTextoBienvenida(activity)
    }

    fun transparentThemeOn(activity: Activity){
        val god = activity.findViewById<ConstraintLayout>(R.id.god)

        god.fitsSystemWindows=true
    }

    fun mostrarBottomBar(activity: Activity) {
        val bottomBar = activity.findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            Animations.setLayoutHeightWithTopMargin(bottomBar, 200, 0)
        },300)
    }

    fun ocultarBottomBar(activity: Activity) {
        val bottomBar = activity.findViewById<AnimatedBottomBar>(R.id.bottom_bar)
        Animations.setLayoutHeightWithTopMargin(bottomBar, 0, 30)
    }

    fun mostrarFabSpeedDial(activity: Activity) {
        val fabSpeedDial = activity.findViewById<FabSpeedDial>(R.id.menu_accion_rapida)

        if(fabSpeedDial.visibility == View.GONE) {

            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                fabSpeedDial.visibility = View.VISIBLE

                Animations.mostrarVistaSuavemente(fabSpeedDial)

            },500)

        }
    }

    fun ocultarFabSpeedDial(activity: Activity) {
        val fabSpeedDial = activity.findViewById<FabSpeedDial>(R.id.menu_accion_rapida)

        if(fabSpeedDial.visibility == View.VISIBLE) {

            Animations.ocultarVistaSuavemente(fabSpeedDial)

            fabSpeedDial.visibility = View.GONE

        }

    }

    fun mostrarBotonAjustes(activity: Activity) {
        val imageView = activity.findViewById<ImageView>(R.id.ir_a_ajustes)

        if(imageView.visibility == View.GONE) {

            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                imageView.visibility = View.VISIBLE

                Animations.mostrarVistaSuavemente(imageView)

            },500)

        }
    }

    fun ocultarBotonAjustes(activity: Activity) {
        val imageView = activity.findViewById<ImageView>(R.id.ir_a_ajustes)

        if(imageView.visibility == View.VISIBLE) {

            Animations.ocultarVistaSuavemente(imageView)

            imageView.visibility = View.GONE

        }

    }

    fun mostrarBotonMenu(activity: Activity) {
        val imageView = activity.findViewById<ImageView>(R.id.openDrawer)

        if(imageView.visibility == View.GONE) {

            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                imageView.visibility = View.VISIBLE

                Animations.mostrarVistaSuavemente(imageView)

            },500)

        }
    }

    fun ocultarBotonMenu(activity: Activity) {
        val imageView = activity.findViewById<ImageView>(R.id.openDrawer)

        if(imageView.visibility == View.VISIBLE) {

            Animations.ocultarVistaSuavemente(imageView)

            imageView.visibility = View.GONE

        }

    }

    fun mostrarTextoBienvenida(activity: Activity) {
        val imageView = activity.findViewById<TextView>(R.id.textoBuenosDias)

        if(imageView.visibility == View.GONE) {

            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                imageView.visibility = View.VISIBLE

                Animations.mostrarVistaSuavemente(imageView)

            },500)

        }
    }

    fun ocultarTextoBienvenida(activity: Activity) {
        val imageView = activity.findViewById<TextView>(R.id.textoBuenosDias)

        if(imageView.visibility == View.VISIBLE) {

            Animations.ocultarVistaSuavemente(imageView)

            imageView.visibility = View.GONE

        }

    }

    fun terminarCarga(loadningAnimation: View, acction: () -> Unit) {

        Animations.ocultarVistaSuavemente(loadningAnimation)

        Handler(Looper.getMainLooper()).postDelayed( {

            loadningAnimation.visibility = View.GONE

            try {
                acction()
            } catch (e: IllegalStateException) {
                Log.e(":::", "¿Tienes un movil o una tostadora? No le dió tiempo a cargar al context")
            }

        },500)
    }

    fun terminarCargaOnError(loadningAnimation: View, noDataFoundTextView: View) {

        Animations.ocultarVistaSuavemente(loadningAnimation, 500)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            noDataFoundTextView.visibility = View.VISIBLE
            Animations.mostrarVistaSuavemente(noDataFoundTextView,500)

        },500)
    }

}