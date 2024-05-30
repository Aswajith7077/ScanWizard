package com.example.qrcodescanner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
//import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.databinding.FragmentRecentBinding
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Recent : Fragment() {

    private lateinit var recentBinding: FragmentRecentBinding;
    private var isFavoriteSelected = false
    private lateinit var myActivity: MainActivity;


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        if(activity!=null)
            myActivity = activity as MainActivity
//        currentContext = requireContext()
        recentBinding = FragmentRecentBinding.inflate(inflater, container, false);

        recentBinding.allRecycler.layoutManager = LinearLayoutManager(requireContext())
        recentBinding.allRecycler.setHasFixedSize(true);
        setRecycleAdapter();


        recentBinding.allScanButton.setOnClickListener{
            if(isFavoriteSelected){
                isFavoriteSelected = false;
                recentBinding.allScanButton.setBackgroundResource(R.drawable.selected_button);
                recentBinding.allScanButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                recentBinding.favoriteButton.setBackgroundResource(R.drawable.unselected_button);
                recentBinding.favoriteButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.transucent))
            }
            setRecycleAdapter()
        }

        recentBinding.favoriteButton.setOnClickListener {
            if(!isFavoriteSelected){
                isFavoriteSelected = true;
                recentBinding.allScanButton.setBackgroundResource(R.drawable.unselected_button);
                recentBinding.allScanButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.transucent))
                recentBinding.favoriteButton.setBackgroundResource(R.drawable.selected_button);
                recentBinding.favoriteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            setRecycleAdapter()
        }

        recentBinding.deleteAllButton.setOnClickListener {
            recentArrayList = mutableListOf()
            favoriteArrayList = mutableListOf()
            setRecycleAdapter()
            db?.clearData()
        }

        return recentBinding.root;
    }
    fun setRecycleAdapter(){
        if(!loaded){
            if(db==null)
                initializeDatabase(requireContext())
            recentArrayList = db?.getData()!!
            for(i in recentArrayList.indices){
                if(recentArrayList[i].isFavorite)
                    favoriteArrayList.add(recentArrayList[i])
            }
            loaded = true
        }
        if(isFavoriteSelected)
            recentBinding.allRecycler.adapter = RecentAdapter(myActivity,favoriteArrayList, Companion.isFavoriteSelected)
        else
            recentBinding.allRecycler.adapter = RecentAdapter(myActivity,recentArrayList, Companion.isFavoriteSelected)

        if(isFavoriteSelected && favoriteArrayList.size == 0 || (!isFavoriteSelected && recentArrayList.size == 0))
            recentBinding.nothingText.visibility = View.VISIBLE
        else
            recentBinding.nothingText.visibility = View.GONE
    }



    companion object {

        @SuppressLint("StaticFieldLeak")

        private lateinit var recyclerView:RecyclerView;
        private var loaded: Boolean = false

        var recentArrayList: MutableList<RecentElements> = mutableListOf();
        var favoriteArrayList = mutableListOf<RecentElements>();
        private var isFavoriteSelected: Boolean = false;
        var db: Database? = null;

        fun initializeDatabase(context: Context) {
            db = Database(context);
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun StoreData(context:Context,valueType:Int, arguments:ArrayList<String>){


            if(db==null){
                initializeDatabase(context);
            }
            if(!loaded){
                recentArrayList = db?.getData()!!
                for(i in recentArrayList.indices){
                    if(recentArrayList[i].isFavorite)
                        favoriteArrayList.add(recentArrayList[i])
                }
                loaded = true
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val date = LocalDateTime.now().format(formatter)
            val id = recentArrayList.size

            var temp:RecentElements
            var i = 0

            while(i<=recentArrayList.size-1){
                if(recentArrayList[i].arguments == arguments) {
                    temp = recentArrayList[i]
                    recentArrayList.remove(temp)
                    i--;
                    if(temp.isFavorite)
                        favoriteArrayList.remove(temp)
                }
                i++;
            }

            recentArrayList.add(0,RecentElements(id,valueType,arguments,false,date))
            for(i in recentArrayList.indices!!)
                db?.addData(recentArrayList[i]);

        }

        fun setFavorite(value: Boolean, id: Int) {
            for (i in recentArrayList.indices!!) {
                if (recentArrayList[i].id == id) {
                    recentArrayList[i].isFavorite = value;
                    if(recentArrayList[i].isFavorite)
                        favoriteArrayList.add(recentArrayList[i])
                    else
                        favoriteArrayList.remove(recentArrayList[i])
                }
            }
        }

    }
}