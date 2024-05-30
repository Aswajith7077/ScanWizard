package com.example.qrcodescanner

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresExtension
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.qrcodescanner.databinding.FragmentScanBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class Scan() : Fragment() {

    private lateinit var scanFragmentBinding: FragmentScanBinding;
    private var imageCapture: ImageCapture? = null;
    private lateinit var cameraExecutor: ExecutorService;
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>;
    private lateinit var myActivity: MainActivity;
    private lateinit var camera: Camera;
    private var maxZoomRatio:Float ?= null;
    private var minZoomRatio:Float? = null;


    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scanFragmentBinding = FragmentScanBinding.inflate(inflater, container, false);
        if (activity != null)
            myActivity = activity as MainActivity;

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                if (!hasPermission(requireContext()))
                    activityResultLauncher.launch(REQUIRED_PERMISSIONS);
                else {
                    val cameraProvider = cameraProviderFuture.get() as ProcessCameraProvider;
                    bindPreview(cameraProvider);
                }
            } catch (e: ExecutionException) {
                Log.e(TAG, "UseCase binding failed", e);
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()));
        scanFragmentBinding.flashlightButton.setOnClickListener {
            if (camera.cameraInfo.hasFlashUnit() && camera.cameraInfo.torchState.value == 0) {
                scanFragmentBinding.flashlightButton.setBackgroundResource(R.drawable.selected_button);
                camera.cameraControl.enableTorch(true);
            } else if (camera.cameraInfo.hasFlashUnit() && camera.cameraInfo.torchState.value != 0) {
                scanFragmentBinding.flashlightButton.setBackgroundResource(R.drawable.unselected_button);
                camera.cameraControl.enableTorch(false);
            }
        }



        scanFragmentBinding.zoomer.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if(maxZoomRatio == null)
                    maxZoomRatio = camera.cameraInfo.zoomState.value?.maxZoomRatio
                if(minZoomRatio == null)
                    minZoomRatio = camera.cameraInfo.zoomState.value?.minZoomRatio
                scanFragmentBinding.percentage.text = "$progress%";
                camera.cameraControl.setZoomRatio(minZoomRatio!!+(progress/100f)*(maxZoomRatio!! - minZoomRatio!!));

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })


        val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val imageUri = it;
            lateinit var image: InputImage;
            try {
                image = InputImage.fromFilePath(requireContext(), imageUri!!);
            } catch (e: IOException) {
                e.printStackTrace()
            }
            scanBarCode(image);

        }


        scanFragmentBinding.galleryButton.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_PICK_IMAGES);
            galleryLauncher.launch("image/*");
        }

        return scanFragmentBinding.root;

    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        val preview = Preview.Builder().build();

        preview.setSurfaceProvider(scanFragmentBinding.viewFinder.surfaceProvider)
        imageCapture =
            ImageCapture.Builder().build()// we need to change this to Image analysis use case

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();
        imageAnalysis.setAnalyzer(
            cameraExecutor,
            MyImageAnalyzer(scanFragmentBinding, activity as MainActivity)
        );
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture, imageAnalysis
        )
    }


    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true;
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                permissionGranted = false
        }
        if (!permissionGranted) {
            Toast.makeText(activity, "Permission Not Granted", Toast.LENGTH_SHORT).show();
        } else {
            val processCameraProvider = cameraProviderFuture.get() as ProcessCameraProvider;
            bindPreview(processCameraProvider);
        }
    }
    companion object {

        private const val TAG = "QRCodeScanner";
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        private val REQUIRED_PERMISSIONS = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
        ).apply{
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }.toTypedArray()


        fun hasPermission(context: Context) = Companion.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun scanBarCode(inputImage: InputImage) {

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner = BarcodeScanning.getClient(options);
        scanner.process(inputImage)
            .addOnSuccessListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    readBarCodeData(it)
                };
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readBarCodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            val valueType = barcode.valueType
            val intent = Intent(activity, ResultViewer::class.java);
            when (valueType) {
                Barcode.TYPE_URL -> {
                    intent.putExtra("TYPE", valueType);
                    intent.putExtra("RAW", rawValue);
                    val dataStore = arrayListOf(rawValue!!);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Recent.StoreData(requireContext(),valueType,dataStore)
                    };
                }

                Barcode.TYPE_TEXT -> {
                    intent.putExtra("TYPE", valueType);
                    intent.putExtra("RAW", rawValue);
                    val dataStore = arrayListOf(rawValue!!);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Recent.StoreData(requireContext(),valueType,dataStore)
                    };
                }

                Barcode.TYPE_PHONE -> {
                    val phone = barcode.phone!!.number;
                    intent.putExtra("TYPE", valueType);
                    intent.putExtra("PHONE", phone);
                    val dataStore = arrayListOf(phone!!);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Recent.StoreData(requireContext(),valueType,dataStore)
                    };
                }

                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType

                    intent.putExtra("TYPE", valueType);
                    intent.putExtra("SSID", ssid);
                    intent.putExtra("PASS", password);
                    intent.putExtra("ENC_TYPE", type)
                    val dataStore = arrayListOf(ssid!!,password!!,type.toString());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Recent.StoreData(requireContext(),valueType,dataStore);
                    };
                }
            }
            MyImageAnalyzer.isrunning = true;
            startActivity(intent);
//            }

//            val result = "Bounds : "+bounds.toString()+"\nCorners : "+corners.toString()+"\nRaw : "+rawValue.toString()+"\nType : "+valueType.toString();
//            mainBinding.resultTv.text = result;
        }
    }
}