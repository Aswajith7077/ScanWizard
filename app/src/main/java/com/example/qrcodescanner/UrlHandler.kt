package com.example.qrcodescanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrcodescanner.databinding.FragmentUrlHandlerBinding


class UrlHandler(private var rawValue:String) : Fragment() {

    private lateinit var urlHandlerBinding: FragmentUrlHandlerBinding;
    private lateinit var myActivity: ResultViewer;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        urlHandlerBinding = FragmentUrlHandlerBinding.inflate(inflater, container, false);
        if(activity!=null)
            myActivity = activity as ResultViewer;
        urlHandlerBinding.urlText.text = rawValue;

        urlHandlerBinding.copyButton.setOnClickListener {
                val clipBoard = myActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager;
                val clipData = ClipData.newPlainText("simple text",urlHandlerBinding.urlText.text);
                clipBoard.setPrimaryClip(clipData);
        }

        urlHandlerBinding.shareButton.setOnClickListener{
            val sendIntent = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,urlHandlerBinding.urlText.text);
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent,null);
            startActivity(shareIntent);
        }

        urlHandlerBinding.openButton.setOnClickListener {
            val openUrl = Intent().apply{
                action = Intent.ACTION_VIEW
                data = Uri.parse(urlHandlerBinding.urlText.text as String?);
            }
            startActivity(openUrl);
        }

        return urlHandlerBinding.root;
    }
}