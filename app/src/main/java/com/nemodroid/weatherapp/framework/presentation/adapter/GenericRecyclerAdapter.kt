package com.nemodroid.weatherapp.framework.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.databinding.CityVerticalItemActionBinding
import com.nemodroid.weatherapp.databinding.HistoryVerticalItemActionBinding
import com.nemodroid.weatherapp.utils.getObject

class GenericRecyclerAdapter : RecyclerView.Adapter<GenericRecyclerAdapter.Holder>, Filterable {

    private val CLICK_TIME_INTERVAL: Long = 500

    private var context: Context
    private var originList: MutableList<Any>
    private var filteredList: MutableList<Any>
    private var selectedClass: Class<*>
    private var onItemClicked: OnItemClicked

    lateinit var viewDataBinding: ViewDataBinding

    private var gson = Gson()

    private var vertical = false

    private var LAST_CLICK_TIME = System.currentTimeMillis()
    private var lastIndex = -1

    constructor(
        context: Context,
        originList: MutableList<Any>,
        selectedClass: Class<*>,
        onItemClicked: OnItemClicked,
        vertical: Boolean
    ) : super() {
        this.context = context
        this.originList = originList
        this.filteredList = originList
        this.selectedClass = selectedClass
        this.onItemClicked = onItemClicked
        this.vertical = vertical
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        for (obj in filteredList)
            when {
                getObject(gson, obj, selectedClass) is City && vertical -> {
                    viewDataBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.city_vertical_item_action,
                        parent,
                        false
                    )
                    break
                }
                getObject(gson, obj, selectedClass) is WeatherHistory && vertical -> {
                    viewDataBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.history_vertical_item_action,
                        parent,
                        false
                    )
                    break
                }
            }

        return Holder(viewDataBinding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: Holder, @SuppressLint("RecyclerView") position: Int) {
        when (val obj = getObject(gson, filteredList[position], selectedClass)) {
            is City -> {
                when (vertical) {
                    true -> {
                        (holder.binding as CityVerticalItemActionBinding).dataModel = obj
                        (holder.binding as CityVerticalItemActionBinding).container.setOnClickListener(
                            object : View.OnClickListener {
                                override fun onClick(it: View?) {
                                    if (System.currentTimeMillis() - LAST_CLICK_TIME < CLICK_TIME_INTERVAL)
                                        return

                                    LAST_CLICK_TIME = System.currentTimeMillis()
                                    lastIndex = position

                                    notifyDataSetChanged()
                                    onItemClicked.onItemClicked(
                                        obj,
                                        it!!.id,
                                        position,
                                        holder.binding
                                    )
                                }

                            })
                        (holder.binding as CityVerticalItemActionBinding).ivHistory.setOnClickListener(
                            object : View.OnClickListener {
                                override fun onClick(it: View?) {
                                    if (System.currentTimeMillis() - LAST_CLICK_TIME < CLICK_TIME_INTERVAL)
                                        return

                                    LAST_CLICK_TIME = System.currentTimeMillis()
                                    lastIndex = position

                                    notifyDataSetChanged()
                                    onItemClicked.onItemClicked(
                                        obj,
                                        it!!.id,
                                        position,
                                        holder.binding
                                    )
                                }

                            })
                        (holder.binding as CityVerticalItemActionBinding).ivDelete.setOnClickListener(
                            object : View.OnClickListener {
                                override fun onClick(it: View?) {
                                    if (System.currentTimeMillis() - LAST_CLICK_TIME < CLICK_TIME_INTERVAL)
                                        return

                                    LAST_CLICK_TIME = System.currentTimeMillis()
                                    lastIndex = position

                                    notifyDataSetChanged()
                                    onItemClicked.onItemClicked(
                                        obj,
                                        it!!.id,
                                        position,
                                        holder.binding
                                    )
                                }

                            })
                    }
                    else -> {}
                }
            }
            is WeatherHistory -> {
                when (vertical) {
                    true -> {
                        (holder.binding as HistoryVerticalItemActionBinding).dataModel = obj
                        (holder.binding as HistoryVerticalItemActionBinding).container.setOnClickListener(
                            object : View.OnClickListener {
                                override fun onClick(it: View?) {
                                    if (System.currentTimeMillis() - LAST_CLICK_TIME < CLICK_TIME_INTERVAL)
                                        return

                                    LAST_CLICK_TIME = System.currentTimeMillis()
                                    lastIndex = position

                                    notifyDataSetChanged()
                                    onItemClicked.onItemClicked(
                                        obj,
                                        it!!.id,
                                        position,
                                        holder.binding
                                    )
                                }

                            })
                        (holder.binding as HistoryVerticalItemActionBinding).ivView.setOnClickListener(
                            object : View.OnClickListener {
                                override fun onClick(it: View?) {
                                    if (System.currentTimeMillis() - LAST_CLICK_TIME < CLICK_TIME_INTERVAL)
                                        return

                                    LAST_CLICK_TIME = System.currentTimeMillis()
                                    lastIndex = position

                                    notifyDataSetChanged()
                                    onItemClicked.onItemClicked(
                                        obj,
                                        it!!.id,
                                        position,
                                        holder.binding
                                    )
                                }

                            })
                    }
                    else -> {}
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(filteredList: MutableList<Any>) {
        this.originList = filteredList
        this.filteredList = filteredList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyInsertedData(filteredList: MutableList<Any>) {
        this.originList = filteredList
        this.filteredList = filteredList
        notifyItemInserted(this.filteredList.size)
    }

    fun notifyInsertedItem(position: Int, any: Any) {
        this.originList.add(position, any)
        this.filteredList.add(position, any)
        notifyItemInserted(position)
        notifyItemRangeInserted(position, filteredList.size)
    }

    fun notifyChangedItem(position: Int, any: Any) {
        this.originList[position] = any
        this.filteredList[position] = any
        notifyItemChanged(position)
        notifyItemRangeChanged(position, filteredList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyRemovedItem(any: Int) {
        try {
            notifyItemRemoved(any)
            notifyItemRangeChanged(any, filteredList.size)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyRemovedAll() {
        this.originList.clear()
        this.filteredList.clear()
        notifyDataSetChanged()
    }

    fun getAdapterBinding(): ViewDataBinding {
        return viewDataBinding
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

    class Holder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClicked {
        fun onItemClicked(
            any: Any,
            viewID: Int,
            position: Int,
            viewDataBinding: ViewDataBinding
        )

        fun onItemLongClicked(
            any: Any,
            viewID: Int,
            position: Int,
            viewDataBinding: ViewDataBinding
        )
    }
}