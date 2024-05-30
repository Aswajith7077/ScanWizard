package com.example.qrcodescanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.qrcodescanner.databinding.FragmentTextHandlerBinding


class TextHandler(private val rawValue:String) : Fragment() {

    private lateinit var textHandlerFragmentBinding:FragmentTextHandlerBinding;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        textHandlerFragmentBinding = FragmentTextHandlerBinding.inflate(inflater, container, false);
        textHandlerFragmentBinding.textArea.text = rawValue

        textHandlerFragmentBinding.copyButton.setOnClickListener{
            val clipBoard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("simple text",textHandlerFragmentBinding.textArea.text);
            clipBoard.setPrimaryClip(clipData);
            Toast.makeText(requireContext(),"Copied to ClipBoard",Toast.LENGTH_SHORT).show();
        }

        textHandlerFragmentBinding.shareButton.setOnClickListener{
            val sendIntent = Intent().apply{
                action = Intent.ACTION_SEND;
                putExtra(Intent.EXTRA_TEXT,textHandlerFragmentBinding.textArea.text);
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent,null);
            startActivity(shareIntent);
        }


        return textHandlerFragmentBinding.root;
    }


}