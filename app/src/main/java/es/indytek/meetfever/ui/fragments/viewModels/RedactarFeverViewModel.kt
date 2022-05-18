package es.indytek.meetfever.ui.fragments.viewModels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import es.indytek.meetfever.data.webservice.WebServiceEmoticono
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentRedactarFeverBinding
import es.indytek.meetfever.models.emoticono.Emoticono
import es.indytek.meetfever.models.emoticono.EmoticonoWrapper
import es.indytek.meetfever.ui.recyclerviews.adapters.IconoRecyclerViewAdapter
import es.indytek.meetfever.utils.Animations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RedactarFeverViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope {
    //job para la coroutines
    private var job: Job = Job()

    //creando el contexto de la coroutines
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private var _emoticonoList: MutableLiveData<MutableList<Emoticono>> = MutableLiveData()

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val emoticonos: LiveData<MutableList<Emoticono>>
        get() = _emoticonoList

    fun setData(emoticonos: MutableList<Emoticono>) {
        _emoticonoList.value = emoticonos
    }

    init {

        launch {
            WebServiceEmoticono.obtenerTodosLosEmoticonos(context, object:
                WebServiceGenericInterface {
                override fun callback(any: Any) {

                    //binding.animationView.visibility = View.GONE

                    if (any == 0) {
                        // TODO ERROR
                    } else {


                        //Animations.ocultarVistaSuavemente(binding.animationView, 500)
                        //Handler(Looper.getMainLooper()).postDelayed(Runnable {

                        val emoticonos = any as EmoticonoWrapper

                        try {

                            _emoticonoList.value = emoticonos

                            Log.d("Emoticonos", "Emoticonos: ${_emoticonoList.value?.size}")

                        } catch (e: Exception) {
                            Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                        }

                        //},600)



                    }

                }
            })
        }

    }

    fun downloadEmoticonos() {

        Thread {

            WebServiceEmoticono.obtenerTodosLosEmoticonos(context, object:
                WebServiceGenericInterface {
                override fun callback(any: Any) {

                    //binding.animationView.visibility = View.GONE

                    if (any == 0) {
                        // TODO ERROR
                    } else {


                        //Animations.ocultarVistaSuavemente(binding.animationView, 500)
                        //Handler(Looper.getMainLooper()).postDelayed(Runnable {

                        val emoticonos = any as EmoticonoWrapper

                        try {

                            _emoticonoList.value = emoticonos

                            Log.d("Emoticonos", "Emoticonos: ${_emoticonoList.value?.size}")

                        } catch (e: Exception) {
                            Log.d(":::","¿Tienes un móvil o una tostadora? no le dió tiempo a cargar al context")
                        }

                        //},600)



                    }

                }
            })

        }.start()

    }
}