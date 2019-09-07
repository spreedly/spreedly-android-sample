package com.spreedly.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.github.javafaker.Faker
import com.spreedly.backend.*
import com.spreedly.backend.resources.*
import com.spreedly.sdk.Spreedly
import com.spreedly.sdk.threeds.lifecycles.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.Serializable

class PurchaseActivity : SpreedlyClientInteractions() {
    private var creditCardSelect: Spinner? = null
    private var amountText: TextView? = null
    private var attempt3dsSwitch: Switch? = null
    private var cvvText: TextView? = null
    private var fullNameText: TextView? = null
    private var gatewayNameText: TextView? = null
    private var monthText: TextView? = null
    private var threeDsVersionText: TextView? = null
    private var yearText: TextView? = null

    private var gateway: Gateway? = null
    private var transaction: Transaction = Transaction(CreditCard())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        setLoading()
        gateway = intent.getSerializableExtra("com.spreedly.sampleapp.gateway") as Gateway

        amountText = findViewById<TextView>(R.id.amountText)
        attempt3dsSwitch = findViewById<Switch>(R.id.attempt3dsSwitch)
        cvvText = findViewById<TextView>(R.id.cvvText)
        fullNameText = findViewById<TextView>(R.id.fullNameText)
        gatewayNameText = findViewById<TextView>(R.id.gatewayName)
        monthText = findViewById<TextView>(R.id.monthText)
        threeDsVersionText = findViewById<TextView>(R.id.threeDsVersionText)
        yearText = findViewById<TextView>(R.id.yearText)

        fullNameText?.text = Faker().name().fullName() as CharSequence

        gatewayNameText?.text = "Transacting on: ${gateway?.name}"
        fullNameText?.requestFocus()

        transaction.callbackUrl = "https://example.com/callback"
        transaction.redirectUrl = "sampleapp://order-pending?gateway_token=${gateway?.token}"

        creditCardSelect = findViewById(R.id.creditCardSelect)
        ArrayAdapter.createFromResource(
            this,
            R.array.adyen_test_cards_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            creditCardSelect?.adapter = it
        }

        setUsable()
    }

    fun runPurchase(view: View) {
        syncTransactionToView()
        setLoading()

        gatewayNameText?.text = "Issuing purchase request"
        doAsync {
            val transaction = client().createGatewayPurchase(gateway?.token, transaction)
            uiThread {
                setUsable()
                startActivity<OrderPending>(
                    "com.spreedly.sampleapp.pending.transaction" to transaction as Serializable,
                    "com.spreedly.sampleapp.pending.gateway" to gateway as Serializable
                )
            }
        }
    }

    private fun syncTransactionToView() {
        transaction.creditCard.fullName = fullNameText?.text.toString()
        transaction.creditCard.number = creditCardSelect?.selectedItem.toString()
        transaction.creditCard.verificationValue = cvvText?.text.toString()
        transaction.creditCard.month = monthText?.text.toString()
        transaction.creditCard.year = yearText?.text.toString()
        transaction.threeDsVersion = threeDsVersionText?.text.toString()
        transaction.attempt3dSecure = attempt3dsSwitch?.isChecked
        transaction.channel = "app"

        if (amountText?.text?.isNotBlank() == true) {
            transaction.amount = amountText?.text.toString().toInt()
        } else {
            transaction.amount = 0
        }
    }

    private fun navigateToSuccess() {
        startActivity<OrderCompleteActivity>(
            "com.spreedly.sampleapp.complete.gateway" to gateway as Serializable
        )
    }

    private fun setLoading() {
        findViewById<Button>(R.id.purchaseButton).isEnabled = false

        findViewById<View>(R.id.creditCardSelect).isEnabled = false
        findViewById<View>(R.id.fullNameText).isEnabled = false
        findViewById<View>(R.id.cvvText).isEnabled = false
        findViewById<View>(R.id.monthText).isEnabled = false
        findViewById<View>(R.id.yearText).isEnabled = false
        findViewById<View>(R.id.threeDsVersionText).isEnabled = false
        findViewById<View>(R.id.attempt3dsSwitch).isEnabled = false
        findViewById<View>(R.id.amountText).isEnabled = false
    }

    private fun setUsable() {
        findViewById<Button>(R.id.purchaseButton).isEnabled = true

        findViewById<View>(R.id.creditCardSelect).isEnabled = true
        findViewById<View>(R.id.fullNameText).isEnabled = true
        findViewById<View>(R.id.cvvText).isEnabled = true
        findViewById<View>(R.id.monthText).isEnabled = true
        findViewById<View>(R.id.yearText).isEnabled = true
        findViewById<View>(R.id.threeDsVersionText).isEnabled = true
        findViewById<View>(R.id.attempt3dsSwitch).isEnabled = true
        findViewById<View>(R.id.amountText).isEnabled = true
    }
}
