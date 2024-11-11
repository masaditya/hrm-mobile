package com.example.hrmapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.data.model.request.Job
import com.hrmapps.databinding.ItemJobBinding

class JobAdapter(private val jobList: List<Job>, private val onClick: (Job) -> Unit) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job) {
            binding.textViewPosition.text = job.position
            binding.textViewDateRange.text = "${job.startDate} - ${job.endDate}"
            binding.root.setOnClickListener { onClick(job) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(jobList[position])
    }

    override fun getItemCount(): Int = jobList.size
}
