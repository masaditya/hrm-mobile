package com.hrmpandjiadhi.ui.viewmodel.notice

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrmpandjiadhi.data.model.notice.Notice
import com.hrmpandjiadhi.data.model.response.NoticeDetailResponse
import com.hrmpandjiadhi.data.repository.notice.NoticeRepository
import kotlinx.coroutines.launch

class NoticeViewModel(private val repository: NoticeRepository) : ViewModel() {

    private val _notices = MutableLiveData<List<Notice>>()
    val notices: LiveData<List<Notice>> get() = _notices

    private val _noticeDetail = MutableLiveData<NoticeDetailResponse>()
    val noticeDetail: LiveData<NoticeDetailResponse> get() = _noticeDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchNotices(token: String, userId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.fetchNotices(token, userId)
            if (result.isSuccess) {
                _notices.value = result.getOrNull()!!
                _isLoading.value = false
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                _isLoading.value = false
            }
        }
    }
    fun markNoticeAsRead(token: String, noticeId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.markNoticeAsRead(token, noticeId)
                if (result.isSuccess) {
                    fetchNotices(token, noticeId)
                    Log.d("NoticeViewModel", "Notice marked as read")
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun fetchDetailNotice(token: String, noticeId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.fetchDetailNotice(token, noticeId)
                if (result.isSuccess) {
                    _noticeDetail.value = result.getOrNull()!!
                    _isLoading.value = false
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error"
                _isLoading.value = false
            }

        }

    }
}