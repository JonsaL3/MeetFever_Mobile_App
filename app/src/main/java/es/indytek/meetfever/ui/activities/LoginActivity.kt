package es.indytek.meetfever.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.indytek.meetfever.data.webservice.WebServiceGenericInterface
import es.indytek.meetfever.data.webservice.WebServiceUsuario
import es.indytek.meetfever.databinding.ActivityLoginBinding
import es.indytek.meetfever.models.usuario.Usuario

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Por defecto, escucho al botón de iniciar sesión
        binding.botonLoguearse.setOnClickListener() {
            iniciarSesion()
        }

    }

    // En caso de que se presione el botón obtengo los campos y me los descargo
    private fun iniciarSesion() {

        val correo = binding.inputEmail.text.toString()
        val contrasena = binding.inputContrasenaLogin.text.toString()

        if (!correo.isNotEmpty() || !contrasena.isNotEmpty()) { // TODO QUITAR EXCLAMACIONES

            // Hago una petición para obtener el usuario
            WebServiceUsuario.inciarSesion("indytek@indytek.indytek", "12345", this, object: WebServiceGenericInterface {
                override fun callback(any: Any) {

                    if (any == 0) { // en caso de que vaya mal muestro un popup

                    } else { // en caso de que vaya bien me voy a la main activity
                        irAMainActivity(any as Usuario)
                    }

                }
            })

        } else { // en caso de que los campos sea empty
            // TODO error
        }

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

    }

}