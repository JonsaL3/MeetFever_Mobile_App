package es.indytek.meetfever.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.ActivityLoginBinding
import es.indytek.meetfever.databinding.ActivityWebBinding
import es.indytek.meetfever.models.usuario.Usuario

class WebActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding
    private lateinit var currentUsuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val webView = binding.webView

        webView.canGoBack()
        webView.loadUrl("https://edu-meetfever.odoo.com/preguntas-frecuentes")

        getUserFromBundel()

        binding.goBack.setOnClickListener {
            backToMainActivity()
        }

    }

    private fun getUserFromBundel(){

        val bundle = intent.extras
        if (bundle != null) {
            currentUsuario = bundle.getSerializable("usuario") as Usuario
        }
    }

    private fun backToMainActivity(){
        // preparlo lo que necesito para irme a la main activity
        val bundle = Bundle()
        val intent = Intent(this, MainActivity::class.java)

        // guardo el usuario en el bundle
        bundle.putSerializable("usuario", currentUsuario)
        intent.putExtras(bundle)

        // me voy a la otra actividad
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}