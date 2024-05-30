package com.example.qrcodescanner

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.barcode.common.Barcode

class RecentAdapter(private val activity: MainActivity,private val arrayList: MutableList<RecentElements>,private val isFavoriteList:Boolean):
    RecyclerView.Adapter<RecentAdapter.RecentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recent_layout,parent,false);
        return RecentViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {

        val current = arrayList[position];
        if(current.isFavorite)
            holder.favoriteButton.setImageResource(R.drawable.like)
        else
            holder.favoriteButton.setImageResource(R.drawable.like_unbordered)
        holder.favoriteButton.setOnClickListener{
            if(current.isFavorite) {
                Recent.setFavorite(false, current.id);
                holder.favoriteButton.setImageResource(R.drawable.like_unbordered)
            }
            else{
                Recent.setFavorite(true,current.id)
                holder.favoriteButton.setImageResource(R.drawable.like)
            }
        }

        holder.heading.setOnClickListener {
            val intent = Intent(activity,ResultViewer::class.java)
            when(current.type){
                Barcode.TYPE_TEXT -> {
                    intent.putExtra("TYPE",current.type);
                    intent.putExtra("RAW", current.arguments?.get(0))
                }
                Barcode.TYPE_WIFI -> {
                    intent.putExtra("TYPE",current.type)
                    intent.putExtra("SSID",current.arguments?.get(0))
                    intent.putExtra("PASS",current.arguments?.get(1))
                    intent.putExtra("ENC_TYPE",current.arguments?.get(2))
                }
                Barcode.TYPE_URL -> {
                    intent.putExtra("TYPE",current.type)
                    intent.putExtra("RAW",current.arguments?.get(0))
                }
                Barcode.TYPE_PHONE -> {
                    intent.putExtra("TYPE",current.type)
                    intent.putExtra("PHONE",current.arguments?.get(0))
                }
            }
            activity.startActivity(intent)
        }

        when(current.type){
            Barcode.TYPE_TEXT -> {
                holder.icon.setImageResource(R.drawable.text_icon);
                holder.heading.text = "Text Resource";
            }
            Barcode.TYPE_WIFI -> {
                holder.icon.setImageResource(R.drawable.wi_fi);
                holder.heading.text = "WIFI Resource";
            }
            Barcode.TYPE_PHONE -> {
                holder.icon.setImageResource(R.drawable.call);
                holder.heading.text = "Phone Number";
            }
            Barcode.TYPE_URL -> {
                holder.icon.setImageResource(R.drawable.web_link_icon);
                holder.heading.text = "URL Resource";
            }
        }
        holder.timingText.text = current.date;
    }

    override fun getItemCount(): Int {
        return arrayList.count();
    }
    class RecentViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val icon = itemView.findViewById<ImageView>(R.id.icon);
        val heading = itemView.findViewById<TextView>(R.id.heading_text);
        val timingText = itemView.findViewById<TextView>(R.id.timing_text);
        val favoriteButton = itemView.findViewById<ImageView>(R.id.favorite_icon);
//        val moreButton = itemView.findViewById<ImageView>(R.id.more_button);
    }
}