package com.spreedly.sampleapp

import androidx.appcompat.app.AppCompatActivity
import com.spreedly.backend.Client
import io.github.cdimascio.dotenv.dotenv

open class SpreedlyClientInteractions: AppCompatActivity(){
    private val env = dotenv {
        directory = "/assets"
        filename = "env"
    }

    protected fun client(): Client {
        return Client(env["SPREEDLY_ENVIRONMENT_KEY"], env["SPREEDLY_ENVIRONMENT_SECRET"], env["SPREEDLY_HOST"], true)
    }
}