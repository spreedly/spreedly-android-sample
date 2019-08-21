package com.spreedly.sampleapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.io.Serializable
import com.spreedly.backend.*
import com.spreedly.backend.resources.*

class MainActivity : AppCompatActivity() {
    private val env = dotenv {
        directory = "/assets"
        filename = "env"
    }

    private var gateways: ArrayList<Gateway> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gatewayListing = findViewById<ListView>(R.id.gatewayListing)

        gatewayListing.setOnItemClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            val selectedGatewayName = gatewayListing.getItemAtPosition(position)
            val selectedGateway = gateways.firstOrNull { it.name == selectedGatewayName }

            if (selectedGateway != null) {
                startActivity<PurchaseActivity>(
                    "com.spreedly.sampleapp.gateway" to selectedGateway as Serializable,
                    "com.spreedly.sampleapp.client" to client() as Serializable
                )
            }
        }
        loadGateways()
    }

    fun loadGateways() {
        val list = findViewById<ListView>(R.id.gatewayListing)
        gateways.clear()

        doAsync {
            gateways = client().listGateways() as ArrayList<Gateway>
            uiThread {
                val arrayAdapter = ArrayAdapter(it, android.R.layout.simple_list_item_1, gateways.map { g -> g.name })
                list.adapter = arrayAdapter
            }
        }
    }

    private fun client(): Client {
        return Client(env["SPREEDLY_ENVIRONMENT_KEY"], env["SPREEDLY_ENVIRONMENT_SECRET"], env["SPREEDLY_HOST"], true)
    }
}
