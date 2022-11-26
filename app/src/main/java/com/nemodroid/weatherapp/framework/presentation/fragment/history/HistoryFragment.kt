package com.nemodroid.weatherapp.framework.presentation.fragment.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.business.domain.data_model.WeatherHistory
import com.nemodroid.weatherapp.databinding.FragmentHistoryBinding
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.framework.presentation.activity.BaseActivity
import com.nemodroid.weatherapp.framework.presentation.adapter.GenericRecyclerAdapter
import com.nemodroid.weatherapp.framework.presentation.fragment.BaseFragment
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import com.nemodroid.weatherapp.utils.capitalizeWord
import com.nemodroid.weatherapp.utils.handler.ErrorModel
import com.nemodroid.weatherapp.utils.handler.LoadModel
import com.nemodroid.weatherapp.utils.views.recyclerview.WrapContentLinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : BaseFragment(), GenericRecyclerAdapter.OnItemClicked, ResultCallback {

    private var bindingProperty: FragmentHistoryBinding? = null

    private val binding get() = bindingProperty!!

    private val databaseInstance by lazy { DatabaseInstance.getInstance(requireActivity()) }

    private var city: City? = null

    private lateinit var viewModel: HistoryViewModel

    private lateinit var loadModel: LoadModel
    private lateinit var errorModel: ErrorModel

    private lateinit var objectList: MutableList<Any>

    private lateinit var historyAdapter: GenericRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bindingProperty = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getExtraArguments()
        setDefaults()
        initList()
        initViewModel()
    }

    private fun getExtraArguments() {
        arguments?.let {
            city =
                Gson().fromJson(it.getString(getString(R.string.gsonCityObject)), City::class.java)

            (requireActivity() as BaseActivity).supportActionBar?.title =
                capitalizeWord("${city?.cityName} ${getString(R.string.textHistorical)}")
        }
    }

    private fun setDefaults() {
        loadModel = LoadModel()
        errorModel = ErrorModel()
        bindHandler()
    }

    private fun bindHandler() {
        binding.loadModel = loadModel
        binding.errorModel = errorModel
    }

    private fun initList() {
        objectList = mutableListOf()
        historyAdapter =
            GenericRecyclerAdapter(
                requireActivity(),
                objectList,
                WeatherHistory::class.java,
                this,
                true
            )

        binding.list.apply {
            layoutManager = WrapContentLinearLayoutManager(
                requireActivity(), 1,
                LinearLayoutManager.VERTICAL, false
            )
            setItemViewCacheSize(20)
            drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
            setHasFixedSize(true)
            adapter = historyAdapter
            itemAnimator = DefaultItemAnimator()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[HistoryViewModel::class.java]
        viewModel.setViewModel(
            requireActivity(),
            isArabic(),
            this
        )

        viewModel.initMutableHistoryList()
        lifecycleScope.launch(Dispatchers.IO) {
            city?.let { viewModel.retrieveHistoryList(databaseInstance, it.cityName) }
            withContext(Dispatchers.Main) {
                viewModel.getMutableHistoryList().collect {
                    objectList = mutableListOf()
                    objectList.addAll(it)
                    historyAdapter.refreshData(objectList)
                }
            }
        }
    }

    private fun navigateToScreen(any: Any) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.gsonCityObject), null)
        bundle.putString(getString(R.string.gsonHistoryObject), Gson().toJson(any))
        findNavController().navigate(R.id.WeatherInfoFragment, bundle)
    }

    //region adapter callback
    override fun onItemClicked(
        any: Any,
        viewID: Int,
        position: Int,
        viewDataBinding: ViewDataBinding
    ) {
        when (any) {
            is WeatherHistory -> {
                when (viewID) {
                    R.id.container,
                    R.id.ivView -> {
                        navigateToScreen(any)
                    }
                }
            }
        }
    }

    override fun onItemLongClicked(
        any: Any,
        viewID: Int,
        position: Int,
        viewDataBinding: ViewDataBinding
    ) {

    }
    //endregion

    //region callback
    override fun onLoad(title: String, message: String) {
        loadModel.showLayout = true
        errorModel.withError = false
        bindHandler()
    }

    override fun onSuccess(message: String, any: Any?) {
        loadModel.showLayout = false
        errorModel.withError = false
        bindHandler()
    }

    override fun onFailure(message: String) {
        loadModel.showLayout = false

        if (message.contains("FAILURE_LOAD_HISTORY_LIST", true))
            setErrorModel(
                errorModel,
                message.substring("FAILURE_LOAD_HISTORY_LIST".length),
                0,
                R.raw.error_404_1,
                false
            )

        bindHandler()
    }
    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProperty = null
    }

}