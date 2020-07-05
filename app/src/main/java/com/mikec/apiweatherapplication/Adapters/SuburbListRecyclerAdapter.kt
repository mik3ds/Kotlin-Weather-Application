package com.mikec.apiweatherapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikec.apiweatherapplication.Models.Suburb
import com.mikec.apiweatherapplication.R

class SuburbListRecyclerAdapter(val context: Context) : RecyclerView.Adapter<SuburbListRecyclerAdapter.ViewHolder>() {

    var selectedSuburb: ((Suburb) -> Unit)? = null
    var suburbList: List<Suburb> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_suburb_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return suburbList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvLocation.text = suburbList[position]._name
        holder.tvWeather.text = suburbList[position]._weatherCondition
        holder.tvTemperature.text = suburbList[position]._weatherTemp + "°"

        if (holder.tvWeather.text == ""){
            holder.tvWeather.text = "-"
        }


    }

    fun setList(suburbList: List<Suburb>) {
        this.suburbList = suburbList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView:View?) : RecyclerView.ViewHolder(itemView!!){
        val tvLocation: TextView = itemView!!.findViewById(R.id.tvLocation)
        val tvWeather: TextView = itemView!!.findViewById(R.id.tvWeather)
        val tvTemperature: TextView = itemView!!.findViewById(R.id.tvTemperature)

        init{
            itemView!!.setOnClickListener{
                selectedSuburb?.invoke(suburbList[adapterPosition])
            }
        }
    }


}