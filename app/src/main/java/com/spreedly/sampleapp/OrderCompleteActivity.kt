package com.spreedly.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.jetbrains.anko.startActivity
import java.io.Serializable
import com.spreedly.backend.*
import com.spreedly.backend.resources.*

class OrderCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_complete)

        val gateway = intent.getSerializableExtra("com.spreedly.sampleapp.complete.gateway") as Gateway

        val restartButton = findViewById<Button>(R.id.orderCompleteRestartButton)
        val restartOnGatewayButton = findViewById<Button>(R.id.orderCompleteRestartGatewayButton)

        restartOnGatewayButton.text = "purchase another on: ${gateway.name}"

        restartButton.setOnClickListener {
            startActivity<MainActivity>()
        }

        restartOnGatewayButton.setOnClickListener {
            startActivity<PurchaseActivity>(
                "com.spreedly.sampleapp.gateway" to gateway as Serializable
            )
        }
    }
}
