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

class PurchaseActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var amountText: TextView? = null
    private var attempt3dsSwitch: Switch? = null
    private var creditCardText: TextView? = null
    private var cvvText: TextView? = null
    private var fullNameText: TextView? = null
    private var gatewayNameText: TextView? = null
    private var monthText: TextView? = null
    private var threeDsVersionText: TextView? = null
    private var yearText: TextView? = null

    private var gateway: Gateway? = null
    private var client: Client? = null
    private var transaction: Transaction = Transaction(CreditCard())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
        setLoading()
        gateway = intent.getSerializableExtra("com.spreedly.sampleapp.gateway") as Gateway
        client = intent.getSerializableExtra("com.spreedly.sampleapp.client") as Client

        amountText = findViewById<TextView>(R.id.amountText)
        attempt3dsSwitch = findViewById<Switch>(R.id.attempt3dsSwitch)
        creditCardText = findViewById<TextView>(R.id.creditCardText)
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
        transaction.redirectUrl = "https://example.com/redirect"

        val spinner: Spinner = findViewById(R.id.threeDsRequestType)
        ArrayAdapter.createFromResource(
            this,
            R.array.three_ds_request_type_array,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
        spinner.onItemSelectedListener = this

        setUsable()
    }

    fun runPurchase(view: View) {
        syncTransactionToView()
        setLoading()

        gatewayNameText?.text = "Issuing purchase request"
        doAsync {
            val newTransaction= client?.createGatewayPurchase(gateway?.token, transaction)
            if (newTransaction == null || newTransaction.state == "gateway_processing_failed") {
                uiThread {
                    Log.d("transaction:failure", "Failed to create transaction")
                    setUsable()
                    gatewayNameText?.text = "Purchase request failed"
                }
            } else if (newTransaction.state == "pending") {
                uiThread {
                    val lifecycle =
                        Spreedly.threeDsInit(it, newTransaction.threeDsContext)
                    gatewayNameText?.text = "Creating device fingerprint"
                    val threeDsData = lifecycle.deviceFingerprintData
                    gatewayNameText?.text = "Sending device fingerprint"
                    runContinue(threeDsData, newTransaction, lifecycle)
                }
            } else if(newTransaction.state == "succeeded") {
                gatewayNameText?.text = "Purchase completed successfully"
                navigateToSuccess()
            }
        }
    }

    fun runContinue(threeDsData: String, newTransaction: Transaction, lifecycle: ILifecycle) {
        doAsync {
            val continuedTransaction = client?.continueTransaction(newTransaction, threeDsData)

            when (continuedTransaction?.state) {
                "succeeded" -> {
                    uiThread {
                        lifecycle.cleanup()
                        gatewayNameText?.text = "Purchase completed successfully"
                        navigateToSuccess()
                    }
                    Log.d("transaction:state", "succeeded")
                }
                "pending" -> {
                    uiThread {
                        gatewayNameText?.text = "Starting challenge flow"
                        lifecycle.doChallenge(it, continuedTransaction?.threeDsContext, object: ChallengeEventHandler() {
                            override fun cancel() {
                                lifecycle.cleanup()
                                setUsable()
                                gatewayNameText?.text = "User aborted challenge flow"
                            }

                            override fun error(type: String) {
                                lifecycle.cleanup()
                                gatewayNameText?.text = "Challenge flow error: $type"
                                setUsable()
                                longToast("There was an error in the challenge flow")
                            }

                            override fun success(threeDsData: String) {
                                doAsync {
                                    client?.continueTransaction(continuedTransaction, threeDsData)
                                    uiThread {
                                        lifecycle.cleanup()
                                        gatewayNameText?.text = "Purchase completed successfully"
                                        navigateToSuccess()
                                    }
                                }
                            }
                        })
                    }
                }
            }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val requestType = parent.getItemAtPosition(pos).toString()

        transaction.channel = requestType
        if (transaction.channel == "none") {
            transaction.channel = ""
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        transaction.channel = ""
    }

    private fun syncTransactionToView() {
        transaction.creditCard.fullName = fullNameText?.text.toString()
        transaction.creditCard.number = creditCardText?.text.toString()
        transaction.creditCard.verificationValue = cvvText?.text.toString()
        transaction.creditCard.month = monthText?.text.toString()
        transaction.creditCard.year = yearText?.text.toString()
        transaction.threeDsVersion = threeDsVersionText?.text.toString()
        transaction.attempt3dSecure = attempt3dsSwitch?.isChecked

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
        findViewById<Button>(R.id.purchaseButton).setBackgroundColor(getResources().getColor(R.color.colorAccent))

        findViewById<View>(R.id.fullNameText).isEnabled = false
        findViewById<View>(R.id.creditCardText).isEnabled = false
        findViewById<View>(R.id.cvvText).isEnabled = false
        findViewById<View>(R.id.monthText).isEnabled = false
        findViewById<View>(R.id.yearText).isEnabled = false
        findViewById<View>(R.id.threeDsVersionText).isEnabled = false
        findViewById<View>(R.id.attempt3dsSwitch).isEnabled = false
        findViewById<View>(R.id.threeDsRequestType).isEnabled = false
        findViewById<View>(R.id.amountText).isEnabled = false
    }

    private fun setUsable() {
        findViewById<Button>(R.id.purchaseButton).isEnabled = true
        findViewById<Button>(R.id.purchaseButton).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark))

        findViewById<View>(R.id.fullNameText).isEnabled = true
        findViewById<View>(R.id.creditCardText).isEnabled = true
        findViewById<View>(R.id.cvvText).isEnabled = true
        findViewById<View>(R.id.monthText).isEnabled = true
        findViewById<View>(R.id.yearText).isEnabled = true
        findViewById<View>(R.id.threeDsVersionText).isEnabled = true
        findViewById<View>(R.id.attempt3dsSwitch).isEnabled = true
        findViewById<View>(R.id.threeDsRequestType).isEnabled = true
        findViewById<View>(R.id.amountText).isEnabled = true
    }
}
