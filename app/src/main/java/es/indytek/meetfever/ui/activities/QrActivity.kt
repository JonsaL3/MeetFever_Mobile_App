package es.indytek.meetfever.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.ActivityQrBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.DialogAcceptCustomActionInterface
import es.indytek.meetfever.utils.DialogMaker

class QrActivity : AppCompatActivity() {


    private lateinit var detector: BarcodeDetector
    private lateinit var context: Context

    private lateinit var binding : ActivityQrBinding

    private var bundle: Bundle? = null

    private var isDialogOpen = false
    private var isCodigoEscaneado = false
    private lateinit var currentUsuario: Usuario

    private val processor = object : Detector.Processor<Barcode> {

        override fun release() {}
        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {

            if (detections != null && detections.detectedItems.isNotEmpty()) {
                val barcode = detections.detectedItems
                if (barcode.size() > 0) {
                    //Log.d(":::QR", barcode.valueAt(0).displayValue)
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        buscarQrEscaneado(barcode.valueAt(0).displayValue)

                    },500)
                }
            }

        }
    }

    private val surfaceCallBack = object : SurfaceHolder.Callback {

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
        override fun surfaceDestroyed(p0: SurfaceHolder) { cameraSource.stop() }


        @SuppressLint("MissingPermission")
        override fun surfaceCreated(p0: SurfaceHolder) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)

                cameraSource.start(binding.cameraSurfaceView.holder)

            else
                requestPermissions(arrayOf(Manifest.permission.CAMERA),1001)
        }

    }

    private lateinit var cameraSource: CameraSource


    override fun onCreate(savedInstanceState: Bundle?) {

        this.setTheme(R.style.Theme_MeetFeverQR)

        super.onCreate(savedInstanceState)
        binding = ActivityQrBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        context = this

        binding.cameraSurfaceView.holder.addCallback(surfaceCallBack)

        getIntentData()

        detector = BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build()
        detector.setProcessor(processor)

        cameraSource = CameraSource.Builder(context,detector)
            .setRequestedFps(25f)
            .setAutoFocusEnabled(true).build()

        binding.cameraSurfaceView.holder.addCallback(surfaceCallBack)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        binding.imageView5.layoutParams.height = height * 2
        binding.imageView5.layoutParams.width = width * 3

        Animations.rotarViewIndefinidamente(binding.imageView5, this)

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                cameraSource.start(binding.cameraSurfaceView.holder)

            } else {
                Toast.makeText(this, "Error con los permisos.", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun buscarQrEscaneado(qr: String) {

        if (!isCodigoEscaneado) {
            isCodigoEscaneado = true
            Handler(Looper.getMainLooper()).postDelayed({

                try {

                    val qrObtenido = qr.split(";")
                    if (qrObtenido[0] != "MEETFEVERMOBILEAPP")
                        throw Exception("QR no válido")

                    val idUsuarioASeguir = qrObtenido[1].toInt()

                    WebServiceUsuario.seguirDejarDeSeguir(currentUsuario.id, idUsuarioASeguir, this, object: WebServiceGenericInterface {
                        override fun callback(any: Any) {

                            Log.d(":::", any.toString())

                            if (any == 0) {
                                DialogMaker(
                                    this@QrActivity,
                                    "Follower Scanner",
                                    "Se ha producido un error al seguir a ese usuario."
                                ).infoCustomAccept(
                                    customAcceptText = getString(R.string.back_to_home),
                                    customAccept = object : DialogAcceptCustomActionInterface{
                                        override fun acceptButton() {

                                            finish()

                                        }
                                    }
                                )
                            } else {

                                if (any.toString().lowercase().contains("true")) {
                                    Log.d(":::", "USUARIO SEGUIDO")
                                    startAnim()
                                } else {
                                    Log.d(":::", "USUARIO DEJADO DE SEGUIR")
                                    DialogMaker(
                                        this@QrActivity,
                                        "Follower Scanner",
                                        "Has dejado de seguir a este usuario."
                                    ).infoCustomAccept(
                                        customAcceptText = getString(R.string.back_to_home),
                                        customAccept = object : DialogAcceptCustomActionInterface{
                                            override fun acceptButton() {

                                                finish()

                                            }
                                        }
                                    )
                                }

                            }

                        }
                    })

                } catch (e: Exception) {
                    e.printStackTrace()
                    DialogMaker(
                        this@QrActivity,
                        "Follower Scanner",
                        "El qr escaneado no está asociado a el ecosistema de MeetFever!"
                    ).infoCustomAccept(
                        customAcceptText = getString(R.string.back_to_home),
                        customAccept = object : DialogAcceptCustomActionInterface{
                            override fun acceptButton() {

                                finish()

                            }
                        }
                    )
                }

            }, 0)
        }

    }

    private fun startAnim(){

        Animations.mostrarVistaSuavemente(binding.fireWorksAnimOn)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {


            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.fireWorks.playAnimation()

            },500)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.dots.playAnimation()

            },800)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.fireWorks3.playAnimation()

            },1100)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.fireWorks5.playAnimation()

            },1400)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                binding.fireWorks4.playAnimation()
                binding.dots3.playAnimation()

                DialogMaker(
                    this@QrActivity,
                    getString(R.string.dialog_hurray),
                    getString(R.string.dialog_started_to_follow)
                ).infoCustomAcceptWithLottie(
                    customAcceptText = getString(R.string.back_to_home),
                    customAccept = object : DialogAcceptCustomActionInterface{
                        override fun acceptButton() {

                            finish()

                        }
                    }
                )

            },1700)

        },500)


    }

    private fun getIntentData(){
        val bundle = intent.extras

        bundle?.let {

            currentUsuario = Gson().fromJson(bundle!!.getString("user").toString(), Usuario::class.java)

        }
    }

}