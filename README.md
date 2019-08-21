# Spreedly Android SDK Example App

This is an example of using the Spreedly Android SDK. There are a couple of
things that you need before running the app

* You need a Spreedly Account
* You need to copy app/src/main/assets/env.example to app/src/main/assets/env
  and change values


*Note* At this time only the Adyen gateway is supported. We'll be adding more
gateway support as time goes on.


## Adyen Test Data:

Valid 3DS 2 credit card: 

* number: 4917610000000000
* cvv: 737
* month: 10
* year: 2020

Flows:

Amount of 12002 - results in a "frictionless" flow
Amount of 12100 - results in a "Basic text authentication" flow
Amount of 12120 - results in a "Basic multi select" flow
Amount of 12130 - results in a "Basic out-of-band (OOB) authentication" flow
Amount of 12150 - results in a "App single select then text authentication" flow
