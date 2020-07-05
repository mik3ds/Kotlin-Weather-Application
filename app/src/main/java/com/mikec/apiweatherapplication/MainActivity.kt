package com.mikec.apiweatherapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mikec.apiweatherapplication.Adapters.CountryFilterRecyclerAdapter
import com.mikec.apiweatherapplication.Adapters.SuburbListRecyclerAdapter
import com.mikec.apiweatherapplication.Models.APIResponse
import com.mikec.apiweatherapplication.Models.Country
import com.mikec.apiweatherapplication.Models.Suburb
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import java.io.Serializable
import java.util.*
import kotlin.Comparator

class MainActivity : AppCompatActivity() {

    private lateinit var context: Context

    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var btnFilterAtoZ: MaterialButton
    private lateinit var btnFilterTemp: MaterialButton
    private lateinit var btnFilterTime: MaterialButton
    private lateinit var btnCloseFilter: MaterialButton

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var suburbListRecyclerAdapter: SuburbListRecyclerAdapter

    private lateinit var selectedCountry: Country
    private lateinit var allSuburbsList: List<Suburb>
    private lateinit var suburbListSortedByName: MutableList<Suburb>
    private lateinit var suburbListSortedByTemp: MutableList<Suburb>
    private lateinit var suburbListSortedByTime: MutableList<Suburb>
    private lateinit var countryFilterList: MutableList<Country>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        setViews()
        init()
    }

    private fun setViews(){
        toggleGroup = findViewById(R.id.toggleGroup)
        swipeRefreshLayout = findViewById(R.id.SwipeContainer)
        recyclerView = findViewById(R.id.RecyclerView)
        btnFilterAtoZ = findViewById(R.id.btnAtoZ)
        btnFilterTemp = findViewById(R.id.btnTemp)
        btnFilterTime = findViewById(R.id.btnTime)
    }

    private fun init(){
        //Set up Suburb RecyclerView with Adapter
        suburbListRecyclerAdapter = SuburbListRecyclerAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = suburbListRecyclerAdapter

        //Starts SuburbDetailView when Suburb is selected from the list
        suburbListRecyclerAdapter.selectedSuburb = {
            val intent = Intent(context, SuburbDetailActivity::class.java).apply {
                putExtra(getString(R.string.suburb), it as Serializable)
            }
            context.startActivity(intent)
        }

        //Begin with Australia as the chosen Country
        selectedCountry = Country("16", "Australia")

        //Pull down to refresh
        swipeRefreshLayout.setOnRefreshListener( SwipeRefreshLayout.OnRefreshListener{
            callWeather()
            Handler().postDelayed( {
                swipeRefreshLayout.isRefreshing = false
            },2000)

        })

        //Listener for Toggle Buttons -> Sort by A-Z, Temperature, Last Updated
        toggleGroup.addOnButtonCheckedListener{_, checkedId, isChecked ->
            if(isChecked){
                when(checkedId){
                    R.id.btnAtoZ -> {
                        suburbListRecyclerAdapter.setList(suburbListSortedByName)
                    }
                    R.id.btnTemp -> {
                        suburbListRecyclerAdapter.setList(suburbListSortedByTemp)
                    }
                    R.id.btnTime -> {
                        suburbListRecyclerAdapter.setList(suburbListSortedByTime)
                    }
                }
            }
        }

        //Listener for Country Filter close button
        btnFilter.setOnClickListener {
            if(countryFilterList.isNotEmpty()){
                showCountryFilter()
            }
        }
        callWeather()
    }

    private fun callWeather(){
        //Use APIInterface to create GET call
        val apiInterface = APIInterface.init().getWeather()
        apiInterface.enqueue( object : retrofit2.Callback<APIResponse> {
            override fun onResponse(call: Call<APIResponse>?, response: Response<APIResponse>?) {
                if(response?.body() != null) {

                    //Initialize Lists
                    allSuburbsList = response.body()!!.data
                    suburbListSortedByName = mutableListOf()
                    suburbListSortedByTemp= mutableListOf()
                    suburbListSortedByTime = mutableListOf()
                    countryFilterList = mutableListOf()


                    for(i in allSuburbsList){
                        //Only add Suburbs from the chosen Country with valid Temperatures to the lists
                        if((i._weatherTemp != null) and (i._country == selectedCountry)){
                            suburbListSortedByName.add(i)
                            suburbListSortedByTemp.add(i)
                            suburbListSortedByTime.add(i)
                        }

                        //Create list of unique Countries
                        if(i._country !in countryFilterList){
                            countryFilterList.add(i._country)
                        }
                    }

                    //Sort Lists
                    Collections.sort(suburbListSortedByName, SortByName())
                    Collections.sort(suburbListSortedByTemp, SortByTemp())
                    Collections.sort(suburbListSortedByTime, SortByTime())
                    Collections.sort(countryFilterList, SortByCountryName())
                    suburbListSortedByTemp.reverse()
                    suburbListSortedByTime.reverse()

                    //Set list after refresh according to the toggle buttons
                    if(btnAtoZ.isChecked){
                        suburbListRecyclerAdapter.setList(suburbListSortedByName)
                    } else if(btnTemp.isChecked){
                        suburbListRecyclerAdapter.setList(suburbListSortedByTemp)
                    } else {
                        suburbListRecyclerAdapter.setList(suburbListSortedByTime)
                    }

                }
            }
            override fun onFailure(call: Call<APIResponse>?, t: Throwable?) {
                swipeRefreshLayout.isRefreshing = false
                if (t != null) Log.e("Error", t.message!!)
            }
        })
    }

    private fun showCountryFilter() {
        //Create layout for Country Filter
        val countryFilter = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val countryFilterView = this.layoutInflater.inflate(R.layout.country_filter_dialog, null)

        //Set up Recycle View and Adapter
        val rvCountryList = countryFilterView.findViewById<RecyclerView>(R.id.rvCountryList)
        val countryAdapter = CountryFilterRecyclerAdapter(context)
        rvCountryList.layoutManager = LinearLayoutManager(this)
        rvCountryList.adapter = countryAdapter
        countryAdapter.setList(countryFilterList)

        //Listener for Country Filter selection
        countryAdapter.selectedCountry = {

            selectedCountry = it

            //When a Country is selected, clear the current lists
            suburbListSortedByName = mutableListOf()
            suburbListSortedByTemp= mutableListOf()
            suburbListSortedByTime = mutableListOf()

            for(i in allSuburbsList){

                //Only add Suburbs from the chosen Country with valid Temperatures to the lists
                if((i._weatherTemp != null) and (i._country._countryID == it._countryID)){
                    suburbListSortedByName.add(i)
                    suburbListSortedByTemp.add(i)
                    suburbListSortedByTime.add(i)
                }
            }

            //Sort the lists
            Collections.sort(suburbListSortedByName, SortByName())
            Collections.sort(suburbListSortedByTemp, SortByTemp())
            Collections.sort(suburbListSortedByTime, SortByTime())
            suburbListSortedByTemp.reverse()
            suburbListSortedByTime.reverse()

            //Set the new Suburb list and close filter
            if(btnAtoZ.isChecked){
                suburbListRecyclerAdapter.setList(suburbListSortedByName)
            } else if(btnTemp.isChecked){
                suburbListRecyclerAdapter.setList(suburbListSortedByTemp)
            } else {
                suburbListRecyclerAdapter.setList(suburbListSortedByTime)
            }
            countryFilter.dismiss()
        }

        //Setup listener for Filter close button
        btnCloseFilter = countryFilterView.findViewById(R.id.btnCloseFilter)
        btnCloseFilter.setOnClickListener{
            countryFilter.dismiss()
        }

        countryFilter.setContentView(countryFilterView)
        countryFilter.show()
    }

    internal class SortByName:
        Comparator<Suburb> {
        override fun compare(o1: Suburb, o2: Suburb): Int {
            return o1._name.compareTo(o2._name)
        }
    }
    internal class SortByTemp:
        Comparator<Suburb> {
        override fun compare(o1: Suburb, o2: Suburb): Int {
            return extractInt(o1) - extractInt(o2)
        }
        fun extractInt(suburb: Suburb): Int{
            val twoDigitTemp = suburb._weatherTemp.replace("\\D".toRegex(), "")
            return if (twoDigitTemp.isEmpty()) 0 else Integer.parseInt(twoDigitTemp)
        }
    }
    internal class SortByTime:
        Comparator<Suburb> {
        override fun compare(o1: Suburb, o2: Suburb): Int {
            return o1._weatherLastUpdated.compareTo(o2._weatherLastUpdated)
        }
    }
    internal class SortByCountryName:
        Comparator<Country> {
        override fun compare(o1: Country, o2: Country): Int {
            return o1._name.compareTo(o2._name)
        }
    }
}