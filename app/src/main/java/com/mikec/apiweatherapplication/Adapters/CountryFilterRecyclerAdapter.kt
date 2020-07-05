package com.mikec.apiweatherapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikec.apiweatherapplication.Models.Country
import com.mikec.apiweatherapplication.R

class CountryFilterRecyclerAdapter(val context: Context) : RecyclerView.Adapter<CountryFilterRecyclerAdapter.ViewHolder>() {

    var selectedCountry: ((Country) -> Unit)? = null
    var countryList: List<Country> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_country_row,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCountry.text = countryList[position]._name
    }

    fun setList(countryList: List<Country>) {
        this.countryList = countryList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView:View?) : RecyclerView.ViewHolder(itemView!!){
        val tvCountry: TextView = itemView!!.findViewById(R.id.tvCountry)

        init{
            itemView!!.setOnClickListener{
                selectedCountry?.invoke(countryList[adapterPosition])
            }
        }
    }


}