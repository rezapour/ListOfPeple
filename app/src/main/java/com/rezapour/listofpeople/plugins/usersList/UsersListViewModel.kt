package com.rezapour.listofpeople.plugins.usersList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rezapour.listofpeople.data.exception.DataProviderException
import com.rezapour.listofpeople.data.repository.UserRepository
import com.rezapour.listofpeople.models.CustomersDomain
import com.rezapour.listofpeople.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val usersRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<List<CustomersDomain>>> = MutableStateFlow(
        UiState.Success(
            emptyList()
        )
    )

    val uiState: StateFlow<UiState<List<CustomersDomain>>> = _uiState

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                _uiState.value = UiState.Success(usersRepository.getCustomers())
            } catch (e: DataProviderException) {
                _uiState.value = UiState.Error(e.messageId)
            } catch (e: Exception) {
                _uiState.value = UiState.DefaultError
            }
        }
    }
}