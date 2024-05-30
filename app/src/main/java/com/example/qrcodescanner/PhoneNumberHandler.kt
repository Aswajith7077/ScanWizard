package com.example.qrcodescanner

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrcodescanner.databinding.FragmentPhoneNumberHandlerBinding

class PhoneNumberHandler(private var phone:String) : Fragment() {

    private lateinit var myActivity: ResultViewer;
    private lateinit var phoneNumberHandlerBinding: FragmentPhoneNumberHandlerBinding;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        phoneNumberHandlerBinding = FragmentPhoneNumberHandlerBinding.inflate(inflater,container, false);
        if(activity!=null)
            myActivity = activity as ResultViewer;

        phoneNumberHandlerBinding.phoneText.text = phone
        phoneNumberHandlerBinding.makeCallButton.setOnClickListener {
            val intent = Intent().apply{
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:"+phone);
            }
            startActivity(intent);
        }

        phoneNumberHandlerBinding.addToContactsButton.setOnClickListener {
            val intent = Intent().apply{
                action = ContactsContract.Intents.Insert.ACTION
                putExtra(ContactsContract.Intents.Insert.PHONE,phone)
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }
            startActivity(intent);
        }

        phoneNumberHandlerBinding.copyButton.setOnClickListener {
            val clipBoard = myActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager;
            val clipData = ClipData.newPlainText("simple text",phone);
            clipBoard.setPrimaryClip(clipData);
        }

        phoneNumberHandlerBinding.shareButton.setOnClickListener {
            val sendIntent = Intent().apply{
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,phone);
                type = "text/plain";
            }
            val shareIntent = Intent.createChooser(sendIntent,null);
            startActivity(shareIntent);
        }

        return phoneNumberHandlerBinding.root;
    }
}