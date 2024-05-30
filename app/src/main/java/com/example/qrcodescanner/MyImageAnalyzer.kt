package com.example.qrcodescanner

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.example.qrcodescanner.databinding.FragmentScanBinding
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class MyImageAnalyzer(private val mainBinding: FragmentScanBinding,private val activity:MainActivity): ImageAnalysis.Analyzer{

    override fun analyze(imageProxy: ImageProxy) {
        scanBarCode(imageProxy);
    }

    @OptIn(ExperimentalGetImage::class)
    private fun scanBarCode(imageProxy: ImageProxy) {
        val image = imageProxy.image;
        if(image!=null){
            val inputImage = InputImage.fromMediaImage(image,imageProxy.imageInfo.rotationDegrees);
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            val scanner = BarcodeScanning.getClient(options);
            scanner.process(inputImage)
                .addOnSuccessListener {
                    readBarCodeData(it);
                }.addOnCompleteListener{
                    imageProxy.close();
                }
        }
    }

    private fun readBarCodeData(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            val valueType = barcode.valueType
            if(!isrunning) {
                val intent = Intent(activity, ResultViewer::class.java);
                when (valueType) {
                    Barcode.TYPE_URL -> {
                        intent.putExtra("TYPE", valueType);
                        intent.putExtra("RAW", rawValue);
                        val dataStore = arrayListOf(rawValue!!);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Recent.StoreData(activity,valueType,dataStore)
                        };
                    }

                    Barcode.TYPE_TEXT -> {
                        intent.putExtra("TYPE", valueType);
                        intent.putExtra("RAW", rawValue);
                        val dataStore = arrayListOf(rawValue!!);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Recent.StoreData(activity,valueType,dataStore)
                        };
                    }

                    Barcode.TYPE_PHONE -> {
                        val phone = barcode.phone!!.number;
                        intent.putExtra("TYPE",valueType);
                        intent.putExtra("PHONE",phone);
                        val dataStore = arrayListOf(phone!!);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Recent.StoreData(activity,valueType,dataStore)
                        };
                    }

                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType

                        intent.putExtra("TYPE", valueType);
                        intent.putExtra("SSID", ssid);
                        intent.putExtra("PASS",password);
                        intent.putExtra("ENC_TYPE",type);
                        val dataStore = arrayListOf(ssid!!,password!!,type.toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Recent.StoreData(activity,valueType,dataStore);
                        };
                    }
                }
                isrunning = true;
                activity.startActivity(intent);
            }

//            val result = "Bounds : "+bounds.toString()+"\nCorners : "+corners.toString()+"\nRaw : "+rawValue.toString()+"\nType : "+valueType.toString();
//            mainBinding.resultTv.text = result;
        }
    }

    companion object{
        var isrunning:Boolean = false;
    }

}