package de.koenidv.expiries

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener

@Suppress("SpellCheckingInspection")
const val REQUEST_TAG = "openfoodfacts"

class NetworkUtils(val context: Context) {


    init {
        AndroidNetworking.initialize(context)
    }

    fun getProductData(barcode: String, callback: (String?) -> Unit) {
        AndroidNetworking.get(context.getString(R.string.url_product_data, barcode))
            .setTag(REQUEST_TAG)
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
        AndroidNetworking.cancel(REQUEST_TAG)

    }

}