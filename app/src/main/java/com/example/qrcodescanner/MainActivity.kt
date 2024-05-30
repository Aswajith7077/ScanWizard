package com.example.qrcodescanner

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private var isScanSelected: Boolean = true;
    private lateinit var mainBinding: ActivityMainBinding;
    private var imageCapture: ImageCapture? = null;
    private lateinit var cameraExecutor: ExecutorService;
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainBinding.scanButton.updatePadding(70, 0, 40, 0);

        if(isScanSelected)
            replaceFragment(Scan())
        else
            replaceFragment(Recent())



        mainBinding.recentButton.setOnClickListener {
            if (isScanSelected) {
                mainBinding.heading.text = "History"
                replaceFragment(Recent())
                mainBinding.recentButton.setBackgroundResource(R.drawable.selected_button);
                mainBinding.recentButton.text = "History"
                mainBinding.recentButton.updatePadding(70, 0, 40, 0);

                mainBinding.scanButton.setBackgroundResource(R.color.background_color);
                mainBinding.scanButton.text = ""
                mainBinding.scanButton.updatePadding(205, 0, 0, 0)

                isScanSelected = false;
            }
        }
        mainBinding.scanButton.setOnClickListener {
            if (!isScanSelected) {

                mainBinding.heading.text = "Scanner"

                replaceFragment(Scan())

                mainBinding.scanButton.setBackgroundResource(R.drawable.selected_button);
                mainBinding.scanButton.updatePadding(70, 0, 40, 0);
                mainBinding.scanButton.text = "Scan"

                mainBinding.recentButton.setBackgroundResource(R.color.background_color);
                mainBinding.recentButton.updatePadding(205, 0, 0, 0)
                mainBinding.recentButton.text = "";

                isScanSelected = true;
            }
        }
    }

    private fun replaceFragment(fragment:Fragment){
        supportFragmentManager.beginTransaction()
            .replace(mainBinding.mainFrameLayout.id,fragment)
            .commitNow()
    }



}