package com.rezapour.listofpeople.plugins.usersList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.rezapour.listofpeople.R
import com.rezapour.listofpeople.databinding.FragmentUserListBinding
import com.rezapour.listofpeople.models.CustomersDomain
import com.rezapour.listofpeople.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null

    private val binding get() = _binding!!

    private val viewModel: UsersListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
    }

    private fun setupFragment() {
        getData()
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        UiState.DefaultError -> onError(R.string.error_default_message)
                        is UiState.Error -> onError(uiState.message)
                        UiState.Loading -> loading(isLoading = true)
                        is UiState.Success -> onSuccess(uiState.data)
                    }
                }
            }
        }
    }

    private fun checkFlow() {
        val uiState = viewModel.uiState.value
        if (uiState is UiState.Success && uiState.data.isEmpty()) onSuccess(uiState.data)
        else getData()
    }

    private fun getData() {
        viewModel.loadData()
    }

    private fun onSuccess(data: List<CustomersDomain>) {
        loading(isLoading = false)
    }

    private fun onError(messageId: Int) {
        loading(isLoading = false)
        showSnackBar(messageId = messageId) {
            getData()
        }
    }

    private fun loading(isLoading: Boolean = true) {
        showSwiper(isLoading)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showSnackBar(messageId: Int, retry: () -> Unit) {
//        Snackbar.make(binding.coordinatorLayout, messageId, Snackbar.LENGTH_INDEFINITE)
//            .setAction(getString(R.string.retry)) {
//                retry()
//            }
//            .show();
    }

    private fun showSwiper(isRefreshing: Boolean) {
//        binding.swiperLayout.isRefreshing = isRefreshing
    }
}
