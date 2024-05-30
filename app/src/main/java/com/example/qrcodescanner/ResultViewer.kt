package com.example.qrcodescanner

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.qrcodescanner.databinding.ActivityResultViewerBinding
import com.google.mlkit.vision.barcode.common.Barcode

class ResultViewer : AppCompatActivity() {
    private lateinit var resultViewerBinding:ActivityResultViewerBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        resultViewerBinding = ActivityResultViewerBinding.inflate(layoutInflater)
        setContentView(resultViewerBinding.root);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        resultViewerBinding.backButton.setOnClickListener{
            terminateActivity()
        }

        val bl: Bundle? = intent.extras
        if(bl!=null){
            val type = bl.getInt("TYPE");

            when(type){
                Barcode.TYPE_TEXT ->{
                    val rawValue = bl.getString("RAW");
                    if(rawValue!=null) {
                        resultViewerBinding.heading.text = "Text Result"
                        replaceFragment(TextHandler(rawValue));
                    }
                }
                Barcode.TYPE_URL ->{
                    val rawValue = bl.getString("RAW");
                    if(rawValue!=null) {
                        resultViewerBinding.heading.text = "URL Result"
                        replaceFragment(UrlHandler(rawValue));
                    }
                }
                Barcode.TYPE_WIFI ->{
                    val ssid = bl.getString("SSID");
                    val password = bl.getString("PASS");
                    val encType = bl.getInt("ENC_TYPE");
                    if(ssid!=null && password!=null && encType!=null){
                        resultViewerBinding.heading.text = "WIFI Result"
                        replaceFragment(WifiHandler(ssid,password,encType));
                    }
                }
                Barcode.TYPE_PHONE -> {
                    val phone = bl.getString("PHONE");
                    if(phone!=null){
                        resultViewerBinding.heading.text = "Phone Result";
                        replaceFragment(PhoneNumberHandler(phone))
                    }
                }
            }
        }

        val onBackPressedCallback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                terminateActivity()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

    }

    private fun terminateActivity() {
        MyImageAnalyzer.isrunning = false;
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(resultViewerBinding.resultFrameLayout.id,fragment)
            .commit()
    }
}