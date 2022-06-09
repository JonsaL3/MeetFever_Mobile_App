package es.indytek.meetfever.ui.fragments.secondaryfragments.experiencia

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PayPalButton
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceEntrada
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.databinding.FragmentPasarelaDePagoBinding
import es.indytek.meetfever.models.entrada.Entrada
import es.indytek.meetfever.models.experiencia.Experiencia
import es.indytek.meetfever.models.persona.Persona
import es.indytek.meetfever.ui.fragments.mainfragments.ExplorerFragment
import es.indytek.meetfever.ui.fragments.secondaryfragments.empresa.AllEmpresasFragment
import es.indytek.meetfever.ui.recyclerviews.adapters.EntradaAdapter
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.Constantes
import es.indytek.meetfever.utils.DialogMaker
import es.indytek.meetfever.utils.Utils
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt


private const val ARG_PARAM1 = "currentUsuario"
private const val ARG_PARAM2 = "numeroDeEntradas"
private const val ARG_PARAM3 = "experiencia"
class PasarelaDePagoFragment : Fragment() {

    // datos que necesito
    private lateinit var currentUsuario: Persona
    private var numeroDeEntradas: Int = 0
    private lateinit var experiencia: Experiencia

    private lateinit var binding: FragmentPasarelaDePagoBinding

    private lateinit var fecha: LocalDateTime

    val listEntradas = mutableListOf<Entrada>()

    private lateinit var payPalButton: PayPalButton

    private lateinit var contexto: Context
    private lateinit var actividad: Activity

    private var cntOkRequests: Int = 0
    private var cntErrorRequests: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUsuario = it.getSerializable(ARG_PARAM1) as Persona
            numeroDeEntradas = it.getSerializable(ARG_PARAM2) as Int
            experiencia = it.getSerializable(ARG_PARAM3) as Experiencia
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasarelaDePagoBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fecha = LocalDateTime.now()

        mostrarFormularioRecycler()

        loadListeners()

        payPalButton = binding.payPalButton

        payPalButton.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.EUR, value = (experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA * numeroDeEntradas).roundToInt().toString())
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i(":::", "CaptureOrderResult: $captureOrderResult")

                    Utils.hideKeyboard(contexto, binding.root)

                    val id = captureOrderResult.toString().split("(")[2].split(",")[0].split("=")[1]

                    listEntradas.forEach {
                        it.idPaypal = id
                    }

                    binding.loadingFakeDialog.visibility = VISIBLE

                    listEntradas.forEach {
                        WebServiceEntrada.insertarCompra(it, contexto, object : WebServiceGenericInterface{
                            override fun callback(any: Any) {

                                if(any.toString() == "") {

                                    cntOkRequests++

                                }else{
                                    cntErrorRequests++
                                }

                                checkStatusWebService(id)

                            }
                        })
                    }

                }
            },
            onCancel = OnCancel {
                Log.v(":::", "OnCancel")
                DialogMaker(
                    contexto,
                    getString(R.string.error_tittle),
                    getString(R.string.error_cancel_purchasee)
                ).infoNoCustomActions()
            },
            onError = OnError { errorInfo ->
                Log.v(":::", "OnError")
                Log.d(":::", "Error details: $errorInfo")
                DialogMaker(
                    contexto,
                    getString(R.string.error_tittle),
                    getString(R.string.purchase_error_message)
                ).infoNoCustomActions()
            }
        )

        Log.d(":::", (experiencia.precio * Constantes.COMISION_MEET_FEVER * Constantes.IVA * numeroDeEntradas).roundToInt().toString())
    }

    private fun mostrarFormularioRecycler(){


        for(i in 1..numeroDeEntradas)
            listEntradas.add(Entrada(
                idPersona = currentUsuario.id,
                idExperiencia = experiencia.id,
                fecha = LocalDateTime.now()
            ))

        listEntradas[0].nombre = currentUsuario.nombre.toString()
        listEntradas[0].apellido1 = currentUsuario.apellido1.toString()
        listEntradas[0].apellido2 = currentUsuario.apellido2.toString()
        listEntradas[0].dni = currentUsuario.dni.toString()

        Animations.pintarLinearRecyclerViewSuavemente(
            linearLayoutManager = LinearLayoutManager(contexto),
            recyclerView = binding.recyclerviewpago,
            adapter = EntradaAdapter(listEntradas, experiencia),
            orientation = LinearLayoutManager.VERTICAL
        )


    }

    private fun checkStatusWebService(id: String){

        Utils.hideKeyboard(contexto, binding.root)

        if((cntOkRequests + cntErrorRequests) == listEntradas.size) {

            if(cntErrorRequests == 0){

                binding.loadingFakeDialog.visibility = GONE

            Animations.mostrarVistaSuavemente(binding.OnApprove)

            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.lottiAnim3.playAnimation()

                Handler(Looper.getMainLooper()).postDelayed(Runnable {

                    Animations.mostrarVistaSuavemente(binding.thaksFakeDialog)

                }, 600)
            }, 400)

            }else{

                binding.loadingFakeDialog.visibility = GONE

                DialogMaker(
                    contexto,
                    getString(R.string.error_tittle),
                    getString(R.string.error_purchase, id)
                ).infoNoCustomActions()
            }
        }
    }

    private fun verifyData(){

        val adapter = binding.recyclerviewpago.adapter as EntradaAdapter

        val list = mutableListOf<Entrada>()
        listEntradas.forEach {
            list.add(it)
        }

        Log.d(":::", list.toString())

        list.removeIf { it.nombre.isEmpty() || it.apellido1.isEmpty() || it.apellido2.isEmpty() || it.dni.isEmpty() }

        if(list.size != listEntradas.size){
            Log.d(":::","ERROR DE DATOS")
            DialogMaker(
                contexto,
                getString(R.string.error_tittle),
                getString(R.string.error_message)
            ).infoNoCustomActions()
        }else{

            //TODO pillar ID de transaccion y mandarlo al WS


                Log.d(":::","TODO OK")

                Utils.hideKeyboard(contexto, binding.root)

                Animations.mostrarVistaSuavemente(binding.layoutConfirmPayment)

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    binding.lottiAnim.playAnimation()
                },400)
        }


    }

    private fun loadListeners(){
        binding.botonPagar.setOnClickListener {
            verifyData()
        }

        binding.botonCancelarPago.setOnClickListener {
            binding.layoutConfirmPayment.visibility = GONE
        }

        binding.botonBackToHome.setOnClickListener {

            val fragmento = ExplorerFragment.newInstance(currentUsuario)
            Utils.cambiarDeFragmentoGuardandoElAnterior((actividad as AppCompatActivity).supportFragmentManager,fragmento, "", R.id.frame_layout)

            val fragmentTransaction = fragmentManager?.beginTransaction()
            fragmentTransaction?.setCustomAnimations(
                R.anim.anim_fade_in,
                R.anim.anim_fade_out,
            )
            fragmentTransaction?.replace(R.id.frame_layout, fragmento, "")
            fragmentTransaction?.commit()
        }



    }



    companion object {
        @JvmStatic
        fun newInstance(currentUsuario: Persona, numeroDeEntradas: Int, experiencia: Experiencia) =
            PasarelaDePagoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, currentUsuario)
                    putSerializable(ARG_PARAM2, numeroDeEntradas)
                    putSerializable(ARG_PARAM3, experiencia)
                }
            }
    }
}