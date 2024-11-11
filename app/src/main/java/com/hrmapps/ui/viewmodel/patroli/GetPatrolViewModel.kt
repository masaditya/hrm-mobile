package com.hrmapps.ui.viewmodel.patroli

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hrmapps.data.model.response.CheckPointResponse
import com.hrmapps.data.repository.patroli.GetPatrolTypesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetPatrolViewModel(private val getPatrolTypesRepository: GetPatrolTypesRepository) : ViewModel()  {
    private val _patrolTypes = MutableLiveData<CheckPointResponse>()
    val patrolTypes: LiveData<CheckPointResponse> get() = _patrolTypes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getPatrolTypes(token: String) {
        _isLoading.value = true
        getPatrolTypesRepository.getPatrolTypes(token).enqueue(object :
            Callback<CheckPointResponse> {
            override fun onResponse(
                p0: Call<CheckPointResponse>,
                p1: Response<CheckPointResponse>
            ) {
                if (p1.isSuccessful) {
                    _isLoading.value = false
                    _patrolTypes.value = p1.body()
                }
                else {
                    _isLoading.value = false
                    _errorMessage.value = p1.message()
                }

            }

            override fun onFailure(p0: Call<CheckPointResponse>, p1: Throwable) {
                _isLoading.value = false
                _errorMessage.value = p1.message
            }

        })

    }


}