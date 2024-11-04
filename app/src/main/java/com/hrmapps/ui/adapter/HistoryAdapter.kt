package com.hrmapps.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.data.model.AttendanceData
import com.hrmapps.databinding.HistoryItemBinding

class HistoryAdapter :
    PagingDataAdapter<AttendanceData, HistoryAdapter.HistoryViewHolder>(HISTORY_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val attendance = getItem(position)
        if (attendance != null) {
            holder.bind(attendance)
        }
    }

    inner class HistoryViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: AttendanceData) {
            binding.apply {
                textViewDate.text = attendance.clock_in_time
                textViewDay.text = attendance.clock_in_time
                textViewStartTime.text = attendance.clock_in_time
                textViewEndTime.text = attendance.clock_out_time
            }
        }
    }

    companion object {
        private val HISTORY_COMPARATOR = object : DiffUtil.ItemCallback<AttendanceData>() {
            override fun areItemsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
                return oldItem.user_id == newItem.user_id
            }

            override fun areContentsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
