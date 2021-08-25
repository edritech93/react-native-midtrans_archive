package com.reactnativemidtrans

import android.util.Log
import com.facebook.react.bridge.*
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import java.util.*

class MidtransModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "Midtrans"
  }

  override fun getConstants(): MutableMap<String, Any> {
    val constants: MutableMap<String, Any> = HashMap()
    val paymentMethod = Arguments.createMap()

    for (i in PaymentMethod.values().indices) {
      paymentMethod.putInt(PaymentMethod.values()[i].name, i)
    }

    constants["PAYMENT_METHOD"] = paymentMethod

    return constants
  }

  @ReactMethod
  fun checkout(options: ReadableMap, promise: Promise) {
    SdkUIFlowBuilder.init()
      .setClientKey(options.getString("client_key"))
      .setContext(reactApplicationContext)
      .setTransactionFinishedCallback {

        if (it.response != null) {
          Log.d("REACT_NATIVE_MIDTRANS", it.response.toString())
        }

        val result = Arguments.createMap()

        result.putBoolean("is_transaction_canceled", it.isTransactionCanceled)

        if (it.source != null) {
          result.putString("source", it.source)
        }

        if (it.status != null) {
          result.putString("status", it.status)
        }

        if (it.statusMessage != null) {
          result.putString("status_message", it.statusMessage)
        }

        if (it.isTransactionCanceled) {
          promise.reject("transaction_canceled", "transaction_canceled")
          return@setTransactionFinishedCallback
        }

        if (it.status == "success") {
          promise.resolve(it.status)
        } else {
          promise.reject(it.status, it.statusMessage)
        }
      }
      .setMerchantBaseUrl(options.getString("merchant_base_url"))
      .enableLog(true)
      .setLanguage(options.getString("language"))
      .buildSDK()

    val setting = MidtransSDK.getInstance().uiKitCustomSetting
    setting.isSkipCustomerDetailsPages = true
    MidtransSDK.getInstance().uiKitCustomSetting = setting

    val items = options.getArray("items")

    var totalPrice = 0.0

    val itemDetailsList: ArrayList<ItemDetails> = ArrayList<ItemDetails>()

    val size = items?.size() ?: 0

    for (i in 0 until size) {
      items?.getMap(i).let {
        val price = it?.getDouble("price") ?: 0.0
        val quality = it?.getDouble("quantity")?.toInt() ?: 0
        val itemDetails = ItemDetails(
          it?.getString("id"),
          price,
          quality,
          it?.getString("name")
        )

        totalPrice += (price * quality)

        itemDetailsList.add(itemDetails)
      }
    }


    val transactionRequest = TransactionRequest(options.getString("order_id") ?: "", totalPrice)

    transactionRequest.itemDetails = itemDetailsList

    MidtransSDK.getInstance().transactionRequest = transactionRequest

    val selectedPaymentMethod = options.getDouble("payment_method") ?: -1.0

    if (selectedPaymentMethod == -1.0) {
      MidtransSDK.getInstance().startPaymentUiFlow(currentActivity)
    } else {
      MidtransSDK.getInstance()
        .startPaymentUiFlow(currentActivity, PaymentMethod.values()[selectedPaymentMethod.toInt()]);
    }
  }


}
