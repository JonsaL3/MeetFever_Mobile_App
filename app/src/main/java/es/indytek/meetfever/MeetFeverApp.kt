package es.indytek.meetfever

import android.app.Application
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

class MeetFeverApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val config = CheckoutConfig(
            application = this,
            clientId = "AVUcl5Nr4hW74QG5RFlMJIlSr1OyD95E6BDg9Su1dQ8dTHRqFzMEtUOEUcfAO3PKVS4FN_HBkZjo3mP2",
            environment = Environment.SANDBOX,
            returnUrl = "es.indytek.meetfever://paypalpay",
            currencyCode = CurrencyCode.EUR,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )

        PayPalCheckout.setConfig(config)

    }
}