package com.example.qrcodescanner

import android.content.Context
import java.io.*

class InternalStorageManager private constructor() {
    fun createFile(context: Context, fileName:String?):Boolean{
        fileName?.let{
            File(context.filesDir,it)
        }
        return true
    }
    fun openFile(context: Context, fileName:String?):File?{
        return fileName?.let{
            File(context.filesDir,it)
        }
    }
    fun writeFile(file:File?,data:String?){
        try{
            val fileWriter = FileWriter(file);
            fileWriter.append(data)
            fileWriter.flush()
            fileWriter.close()
        }catch(e:IOException){
            e.printStackTrace()
        }
    }
    fun readFile(file:File?):StringBuilder{
        var line:String?
        val stringBuilder = StringBuilder();
        try{
            val fileReader = FileReader(file)
            val bufferedReader = BufferedReader(fileReader)
            while(bufferedReader.readLine().also{line = it}!=null){
                stringBuilder.append(line)
            }
        }catch(e:IOException){
            e.printStackTrace()
        }
        return stringBuilder;
    }

    fun createFileInDirectory(context:Context,fileName:String?):Boolean {
        val directory = context.filesDir
        val file = fileName?.let {
            File(directory,it)
        }
        return true
    }

    companion object{
        var instance:InternalStorageManager? = null
            get(){
                if(field == null){
                    field = InternalStorageManager()
                }
                return field
            }
        private set
    }
}