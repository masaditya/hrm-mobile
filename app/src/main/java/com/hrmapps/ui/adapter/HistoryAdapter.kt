package com.hrmapps.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.data.model.AttendanceData
import com.hrmapps.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter : ListAdapter<AttendanceData, HistoryAdapter.HistoryViewHolder>(HISTORY_COMPARATOR) {

    private var attendanceList: List<AttendanceData> = emptyList()

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
                textViewDate.text = dateFormat(attendance.clock_in_time) ?: ""
                textViewDay.text = dayFormat(attendance.clock_in_time) ?: ""
                textViewStartTime.text = hourFormat(attendance.clock_in_time) ?: "-"
                textViewEndTime.text = hourFormat(attendance.clock_out_time) ?: "-"

            }
        }
    }

    private fun dayFormat(dateTime: String?): String? {
        if (dateTime.isNullOrEmpty()) return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateTime)

        val dayFormat = SimpleDateFormat("E", Locale.getDefault())
        return dayFormat.format(date!!)
    }

    private fun dateFormat(dateTime: String?): String? {
        if (dateTime.isNullOrEmpty()) return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateTime)

        val dateOnlyFormat = SimpleDateFormat("dd", Locale.getDefault())
        return dateOnlyFormat.format(date!!)
    }

    private fun hourFormat(dateTime: String?): String? {
        if (dateTime.isNullOrEmpty()) return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateTime)

        val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return hourFormat.format(date!!)
    }

    fun submitData(list: List<AttendanceData>) {
        attendanceList = list
        notifyDataSetChanged()
    }
    companion object {
        private val HISTORY_COMPARATOR = object : DiffUtil.ItemCallback<AttendanceData>() {
            override fun areItemsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
                return oldItem.user_id == newItem.user_id && oldItem.clock_in_time == newItem.clock_in_time
            }

            override fun areContentsTheSame(oldItem: AttendanceData, newItem: AttendanceData): Boolean {
                return oldItem == newItem
            }
        }
    }
}
