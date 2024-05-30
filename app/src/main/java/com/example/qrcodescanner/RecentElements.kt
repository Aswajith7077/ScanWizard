package com.example.qrcodescanner

data class RecentElements(
    var id:Int = -1,
    var type:Int = 0,
    var arguments: ArrayList<String>? = null,
    var isFavorite:Boolean = false,
    var date:String = ""
){}
