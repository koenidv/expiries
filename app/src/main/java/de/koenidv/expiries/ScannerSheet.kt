package de.koenidv.expiries

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.json.JSONObject
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RuntimePermissions
class ScannerSheet(val scannedCallback: (JSONObject) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var preview: PreviewView
    private lateinit var loadingProgress: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sheet_camera, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        preview = view.findViewById(R.id.camera_preview)
        loadingProgress = view.findViewById(R.id.loading_progress)
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCameraWithPermissionCheck()

        return view
    }

    override fun onDestroy() {
        cameraExecutor.shutdown()
        super.onDestroy()
    }

    @NeedsPermission(android.Manifest.permission.CAMERA)
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(preview.surfaceProvider)
                }

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8
                ).build()
            val scanner = BarcodeScanning.getClient(options)

            AndroidNetworking.initialize(context)
            var lastResult: String? = null

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(scanner, imageProxy) { result ->

                            if (!result.equals(lastResult)) {

                                lastResult = result

                                if (result == null) {
                                    loadingProgress.visibility = View.GONE
                                    return@processImageProxy
                                }

                                AndroidNetworking.cancel("openfoodfacts")

                                if (loadingProgress.visibility != View.VISIBLE)
                                    loadingProgress.visibility = View.VISIBLE

                                AndroidNetworking.get("https://world.openfoodfacts.org/api/v0/product/${result}.json")
                                    .setTag("openfoodfacts")
                                    .build()
                                    .getAsJSONObject(object : JSONObjectRequestListener {
                                        override fun onResponse(response: JSONObject) {
                                            loadingProgress.visibility = View.GONE
                                            dismiss()
                                            scannedCallback(response)
                                        }

                                        override fun onError(anError: ANError) {
                                            Log.e("Scanner", anError.errorDetail)
                                        }
                                    })
                            }
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy,
        callback: (String?) -> Unit
    ) {
        // Process the image and callback with the result
        // Close the proxy after completion to allow for the next image
        barcodeScanner.process(
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        )
            .addOnSuccessListener { barcodes -> callback(barcodes.firstOrNull()?.rawValue) }
            .addOnFailureListener { callback(null) }
            .addOnCompleteListener { imageProxy.close() }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

}