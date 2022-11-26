package com.nemodroid.weatherapp.framework.presentation.fragment.cities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.View.OnClickListener
import android.window.SplashScreen
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.DeviceProperties
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nemodroid.weatherapp.R
import com.nemodroid.weatherapp.business.domain.data_model.City
import com.nemodroid.weatherapp.databinding.AddNewCityDialogBinding
import com.nemodroid.weatherapp.databinding.FragmentCityBinding
import com.nemodroid.weatherapp.framework.offline_database.DatabaseInstance
import com.nemodroid.weatherapp.framework.presentation.adapter.GenericRecyclerAdapter
import com.nemodroid.weatherapp.framework.presentation.fragment.BaseFragment
import com.nemodroid.weatherapp.utils.callbacks.ResultCallback
import com.nemodroid.weatherapp.utils.handler.ErrorModel
import com.nemodroid.weatherapp.utils.handler.LoadModel
import com.nemodroid.weatherapp.utils.showToast
import com.nemodroid.weatherapp.utils.views.recyclerview.WrapContentLinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CityFragment : BaseFragment(), GenericRecyclerAdapter.OnItemClicked, ResultCallback,
    OnClickListener {

    private var bindingProperty: FragmentCityBinding? = null

    private val binding get() = bindingProperty!!

    private val databaseInstance by lazy { DatabaseInstance.getInstance(requireActivity()) }

    private lateinit var viewModel: CityViewModel

    private lateinit var loadModel: LoadModel
    private lateinit var errorModel: ErrorModel

    private lateinit var objectCityList: MutableList<Any>

    private lateinit var cityAdapter: GenericRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bindingProperty = FragmentCityBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnCreateOptionMenu()
        setDefaults()
        initActions()
        initList()
        initViewModel()
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

    private fun initActions() {
        binding.errorContainer.btnErrorAction.setOnClickListener(this)
    }

    private fun initList() {
        objectCityList = mutableListOf()
        cityAdapter =
            GenericRecyclerAdapter(
                requireActivity(),
                objectCityList,
                City::class.java,
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
            adapter = cityAdapter
            itemAnimator = DefaultItemAnimator()
        }

    }

    private fun setupAddNewCityDialog() {
        val dialogBinding: AddNewCityDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireActivity()), R.layout.add_new_city_dialog, null, false
        )

        val dialog = Dialog(requireActivity())
        val window: Window = dialog.window!!

        // Set dialog size
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding.root)
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION") val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION") display.getMetrics(displayMetrics)
        }
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(window.attributes)
        var dialogWindowWidth = (displayWidth * 0.8f).toInt()
        var dialogWindowHeight = (displayHeight * .8f).toInt()
        if (DeviceProperties.isTablet(resources)) {
            dialogWindowWidth = (displayWidth * 0.6f).toInt()
            dialogWindowHeight = (displayHeight * 6.4f).toInt()
        }
        layoutParams.width = dialogWindowWidth
        window.attributes = layoutParams

        dialogBinding.tvAdd.setOnClickListener {
            when {
                dialogBinding.inputName.text.toString().trim().isEmpty() -> {
                    showToast(requireActivity(), getString(R.string.actionEnterCityName))
                }
                else -> {
                    insertNewCity(dialogBinding.inputName.text.toString().lowercase().trim())
                    dialog.cancel()
                }
            }
        }

        dialogBinding.tvBack.setOnClickListener {
            dialog.cancel()
        }

        if (!dialog.isShowing)
            dialog.show()
    }

    private fun insertNewCity(city: String) {
        viewModel.insertNewCity(databaseInstance, city)
    }

    private fun deleteCity(city: City) {
        viewModel.deleteCity(databaseInstance, city)
    }

    private fun setOnCreateOptionMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                menuItemsIconColor(menu, toolbarMenuColor)
            }

            override fun onPrepareMenu(menu: Menu) {
                menuItemsIconColor(menu, toolbarMenuColor)
                super.onPrepareMenu(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        setupAddNewCityDialog()
                        return true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[CityViewModel::class.java]
        viewModel.setViewModel(
            requireActivity(),
            isArabic(),
            this
        )

        viewModel.initMutableCityList()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.retrieveCityList(databaseInstance)

            withContext(Dispatchers.Main) {
                viewModel.getMutableCityList().collect {
                    objectCityList = mutableListOf()
                    objectCityList.addAll(it)
                    cityAdapter.refreshData(objectCityList)
                }
            }
        }
    }

    private fun confirmDeleteCity(city: City) {
        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())

        dialogBuilder.setMessage(
            String.format(
                getString(R.string.alertConfirmDeleteThisCity),
                city.cityName
            )
        )

        dialogBuilder.setPositiveButton(R.string.actionConfirm) { dialog, which ->
            deleteCity(city)
        }

        dialogBuilder.setNegativeButton(R.string.actionDismiss) { dialog, which ->

        }

        if (!dialogBuilder.show().isShowing) {
            dialogBuilder.show()
        }
    }

    private fun navigateToScreen(viewID: Int, any: Any) {
        val bundle = Bundle()
        bundle.putString(getString(R.string.gsonCityObject), Gson().toJson(any))
        bundle.putString(getString(R.string.gsonHistoryObject), null)
        when (viewID) {
            R.id.container -> {
                findNavController().navigate(R.id.WeatherInfoFragment, bundle)
            }
            R.id.ivHistory -> {
                findNavController().navigate(R.id.HistoryFragment, bundle)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnErrorAction -> {
                setupAddNewCityDialog()
            }
        }
    }

    //region adapter callback
    override fun onItemClicked(
        any: Any,
        viewID: Int,
        position: Int,
        viewDataBinding: ViewDataBinding
    ) {
        when (any) {
            is City -> {
                when (viewID) {
                    R.id.container -> {
                        navigateToScreen(viewID, any)
                    }
                    R.id.ivHistory -> {
                        navigateToScreen(viewID, any)
                    }
                    R.id.ivDelete -> {
                        confirmDeleteCity(any)
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

        if (message.contains("FAILURE_LOAD_CITY_LIST", true))
            setErrorModel(
                errorModel,
                message.substring("FAILURE_LOAD_CITY_LIST".length),
                0,
                R.raw.error_404_3,
                true,
                getString(R.string.actionAddNewCity)
            )

        bindHandler()
    }
    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        bindingProperty = null
    }

}