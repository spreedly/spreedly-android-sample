package com.spreedly.sampleapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.spreedly.backend.resources.Gateway
import com.spreedly.backend.resources.Transaction
import com.spreedly.sdk.Spreedly
import com.spreedly.sdk.threeds.lifecycles.ChallengeEventHandler
import com.spreedly.sdk.threeds.lifecycles.ILifecycle
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.Serializable

class OrderPending : SpreedlyClientInteractions() {

    var loadingText: TextView? = null
    var gateway: Gateway? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_pending)

        val pendingTransactionTokenText = findViewById<TextView>(R.id.pendingTransactionTokenText)
        val pendingGatewayTokenText = findViewById<TextView>(R.id.pendingGatewayTokenText)
        loadingText = findViewById(R.id.pendingLoadingText)

        val action: String? = intent?.action
        val data: Uri? = intent?.data
        // Supplied by Spreedly's redirect
        // Supplied by this app when creating the redirect URL

        val existingGateway = intent.getSerializableExtra("com.spreedly.sampleapp.pending.gateway") as Gateway
        val existingTransaction = intent.getSerializableExtra("com.spreedly.sampleapp.pending.transaction") as Transaction
        val transactionToken = resolveTransactionToken(data?.getQueryParameter("transaction_token"), existingTransaction)
        val gatewayToken = resolveGatewayToken(data?.getQueryParameter("gateway_token"), existingGateway)

        pendingTransactionTokenText.text = "Transaction: ${transactionToken}"
        pendingTransactionTokenText.text = "Transaction: ${transactionToken}"

        doAsync {
            gateway = client().showGateway(gatewayToken)
            val transaction = client().showTransaction(transactionToken);

            uiThread {
                val lifecycle = Spreedly.threeDsInit(it, transaction?.threeDsContext)
                handleTransaction(transaction, lifecycle)
            }
        }
    }

    private fun handleTransaction(transaction: Transaction?, lifecycle: ILifecycle?) {
        if (transaction == null || transaction.state == "gateway_processing_failed") {
            Log.d("transaction:failure", "Failed to create transaction")
            loadingText?.text = "Purchase request failed"
        } else if (transaction.succeeded) {
            lifecycle?.cleanup()
            loadingText?.text = "Purchase completed successfully"
            navigateToSuccess()
            Log.d("transaction:state", "succeeded")
        } else if (transaction.state == "pending" && transaction.requiredAction == "device_fingerprint") {
            val newLifecycle = Spreedly.threeDsInit(this, transaction.threeDsContext)
            loadingText?.text = "Creating device fingerprint"
            val threeDsData = newLifecycle.deviceFingerprintData
            loadingText?.text = "Sending device fingerprint"
            runContinue(threeDsData, transaction, newLifecycle)
        } else if (transaction.state == "pending" && transaction.requiredAction == "redirect") {
            lifecycle?.doRedirect(this, transaction.checkoutForm, transaction.checkoutUrl)
        } else if (transaction.state == "pending" && transaction.requiredAction == "challenge") {
            loadingText?.text = "Starting challenge flow"
            lifecycle?.doChallenge(this, transaction?.threeDsContext, object: ChallengeEventHandler() {
                override fun cancel() {
                    lifecycle.cleanup()
                    loadingText?.text = "User aborted challenge flow"
                }

                override fun error(type: String) {
                    lifecycle.cleanup()
                    loadingText?.text = "Challenge flow error: $type"
                    longToast("There was an error in the challenge flow")
                }

                override fun success(threeDsData: String) {
                    doAsync {
                        client().continueTransaction(transaction, threeDsData)
                        uiThread {
                            lifecycle.cleanup()
                            loadingText?.text = "Purchase completed successfully"
                            navigateToSuccess()
                        }
                    }
                }
            })
        }
    }

    private fun runContinue(threeDsData: String, newTransaction: Transaction, lifecycle: ILifecycle) {
        doAsync {
            val continuedTransaction = client().continueTransaction(newTransaction, threeDsData)

            uiThread {
                handleTransaction(continuedTransaction, lifecycle)
            }
        }
    }

    private fun navigateToSuccess() {
        startActivity<OrderCompleteActivity>(
            "com.spreedly.sampleapp.complete.gateway" to gateway as Serializable
        )
    }

    private fun resolveTransactionToken(token: String?, transaction: Transaction?): String {
        if (!token.isNullOrBlank()) {
            return token
        } else if (!transaction?.token?.isBlank()!!) {
            return transaction.token
        }

        return ""
    }

    private fun resolveGatewayToken(token: String?, gateway: Gateway?): String {
        if (!token.isNullOrBlank()) {
            return token
        } else if (!gateway?.token?.isBlank()!!) {
            return gateway.token
        }

        return ""
    }

}
