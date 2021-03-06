package es.indytek.meetfever.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import es.indytek.meetfever.R
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.ActivityLoginBinding
import es.indytek.meetfever.models.usuario.Usuario
import es.indytek.meetfever.utils.Animations
import es.indytek.meetfever.utils.CreateMD5
import es.indytek.meetfever.utils.DialogMaker

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // cierro la pantalla de carga
        //Animations.esperarYOcultarVistaSuavemente(binding.prePantallaDeCarga, 3000)

        listenButtons()

    }

    // todos los botones de esta pantalla los escucho
    private fun listenButtons() {

        // Por defecto, escucho al botón de iniciar sesión
        binding.botonLoguearse.setOnClickListener() {
            iniciarSesion()
        }

        binding.botonIrARegistrarse.setOnClickListener() {
            irARegistrarse()
        }

    }

    // En caso de que se presione el botón obtengo los campos y me los descargo
    private fun iniciarSesion() {

        val correo = binding.inputEmail.text.toString()
        val contrasena = CreateMD5().create(binding.inputContrasenaLogin.text.toString())

        if (correo.isNotEmpty() || contrasena.isNotEmpty()) { // TODO QUITAR EXCLAMACIONES

            Animations.mostrarVistaSuavemente(binding.prePantallaDeCarga, 500)

            // Hago una petición para obtener el usuario
            WebServiceUsuario.inciarSesion(correo, contrasena, this, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) { // en caso de que vaya mal muestro un popup
                        DialogMaker(this@LoginActivity, getString(R.string.atencion), getString(R.string.creedenciales_erroneas)).infoNoCustomActions()
                        Animations.ocultarVistaSuavemente(binding.prePantallaDeCarga, 500)
                    } else { // en caso de que vaya bien me voy a la main activity

                        Handler(Looper.getMainLooper()).postDelayed({
                            irAMainActivity(any as Usuario)
                        },2000)

                    }

                }
            })

        } else { // en caso de que los campos sea empty
            Toast.makeText(this, "Hay campos vacíos", Toast.LENGTH_SHORT).show()
        }

    }

    // me muevo a la actividad de registra
    private fun irARegistrarse() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    // Me muevo a la main activity si se ha iniciado sesión correctamente
    private fun irAMainActivity(usuario: Usuario) {

        // preparlo lo que necesito para irme a la main activity
        val bundle = Bundle()
        val intent = Intent(this, MainActivity::class.java)

        // guardo el usuario en el bundle
        bundle.putSerializable("usuario", usuario)
        intent.putExtras(bundle)

        // me voy a la otra actividad
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }

}