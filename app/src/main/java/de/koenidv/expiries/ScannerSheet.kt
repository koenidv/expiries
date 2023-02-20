package de.koenidv.expiries

import  android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import de.koenidv.expiries.databinding.SheetScannerBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RuntimePermissions
class ScannerSheet(val scannedCallback: (String?) -> Unit) : BottomSheetDialogFragment() {

    private var _binding: SheetScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetScannerBinding.inflate(inflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)

        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCameraWithPermissionCheck()

        binding.addManuallyButton.setOnClickListener {
            scannedCallback(null)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    @NeedsPermission(android.Manifest.permission.CAMERA)
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            bindUseCases(cameraProviderFuture.get())
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindUseCases(camera: ProcessCameraProvider) {
        try {
            camera.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                getPreviewUseCase(),
                getImageAnalyzer()
            )
        } catch (exc: Exception) {
            Log.e("Camera", "Use case binding failed", exc)
        }
    }

    private fun getPreviewUseCase(): Preview {
        val view = binding.cameraPreview
        return Preview.Builder()
            .setTargetResolution(Size(view.width, view.height))
            .build()
            .also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
    }

    private var scanner: BarcodeScanner? = null
    private fun getBarcodeScanner(): BarcodeScanner {
        if (scanner != null) return scanner!!

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8
            ).build()
        scanner = BarcodeScanning.getClient(options)
        return scanner!!
    }

    private fun getImageAnalyzer(): ImageAnalysis {
        val network = NetworkUtils(requireContext())
        var lastResult: String? = null

        val analysis = ImageAnalysis.Builder().build()
        analysis.setAnalyzer(cameraExecutor) { image ->
            processImage(image) { result ->

                if (result.equals(lastResult)) return@processImage
                lastResult = result
                if (result == null) return@processImage

                handleBarcodeScanned(result, network)
            }
        }

        return analysis
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImage(image: ImageProxy, callback: (String?) -> Unit) {
        getBarcodeScanner().process(
            InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
        )
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull() ?: return@addOnSuccessListener
                callback(barcode.rawValue)
            }
            .addOnFailureListener { callback(null) }
            .addOnCompleteListener { image.close() }
    }

    private fun handleBarcodeScanned(barcode: String, network: NetworkUtils) {
        network.cancelProductDataRequests()
        binding.loadingProgress.visibility = View.VISIBLE

        network.getProductData(barcode) { productData ->
            if (productData == null) {
                binding.loadingProgress.visibility = View.GONE
            } else {
                scannedCallback(productData)
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        _binding = null
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

}