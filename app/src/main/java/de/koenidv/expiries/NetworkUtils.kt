package de.koenidv.expiries

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener

class NetworkUtils(context: Context) {

    init {
        AndroidNetworking.initialize(context)
    }

    fun getProductData(barcode: String, callback: (String?) -> Unit) {
        AndroidNetworking.get("https://world.openfoodfacts.org/api/v0/product/${barcode}.json")
            .setTag("openfoodfacts")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    callback(response)
                }

                override fun onError(anError: ANError) {
                    Log.e("Scanner", anError.errorDetail)
                    Log.w("Scanner", "No product found")
                    callback(null)
                }
            })
    }

    fun cancelProductDataRequests() {
        AndroidNetworking.cancel("openfoodfacts")

    }

}