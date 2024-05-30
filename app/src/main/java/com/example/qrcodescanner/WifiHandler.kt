package com.example.qrcodescanner

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_ADD_NETWORKS
import android.provider.Settings.ADD_WIFI_RESULT_SUCCESS
import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST
import android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST
import android.provider.Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.example.qrcodescanner.databinding.FragmentWifiHandlerBinding


class WifiHandler(private val ssid:String,private val password:String,private val encType:Int) : Fragment() {

    private lateinit var myActivity:ResultViewer;
    private lateinit var wifiBinding:FragmentWifiHandlerBinding;
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        wifiBinding = FragmentWifiHandlerBinding.inflate(inflater, container, false);

        wifiBinding.ssidText.text = ssid;
        wifiBinding.passwordText.text = password;
        if(activity!=null)
            myActivity = activity as ResultViewer;

        if(encType==2)
            wifiBinding.encryptionText.text = "WPA/WPA2";
        else
            wifiBinding.encryptionText.text = "WEP";

//        wifiBinding.encryptionText.text = encType.toString();
//        wifiBinding.joinButton.setOnClickListener{
//
//
//            var suggestions:WifiNetworkSuggestion;
//
//            if(encType==2) {
//                suggestions = WifiNetworkSuggestion.Builder()
//                    .setSsid(ssid)
//                    .setWpa2Passphrase(password)
//                    .build()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    val intent = Intent().apply {
//
//                        action = ACTION_WIFI_ADD_NETWORKS
//                        putExtra(EXTRA_WIFI_NETWORK_LIST, suggestions)
//                    }
//                    startActivity(intent)
//                }
//            }
//            else{
//                suggestions = WifiNetworkSuggestion.Builder()
//                    .setSsid(ssid)
//                    .build()
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    val intent = Intent().apply {
//
//                        action = ACTION_WIFI_ADD_NETWORKS
//                        putExtra(EXTRA_WIFI_NETWORK_LIST, suggestions)
//                    }
//                    startActivity(intent)
//                }
//
//            }
//        }
        return wifiBinding.root;
    }

}