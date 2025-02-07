package com.hrmpandjiadhi.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.model.leave.Leave
import com.hrmpandjiadhi.databinding.ItemRiwayatCutiBinding

class LeaveAdapter : RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder>() {

    private val leaveList = mutableListOf<Leave>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = ItemRiwayatCutiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val leave = leaveList[position]
        holder.bind(leave)
    }

    override fun getItemCount() = leaveList.size

    fun setData(newLeaveList: List<Leave>) {
        leaveList.clear()
        leaveList.addAll(newLeaveList)
        notifyDataSetChanged()
    }

    inner class LeaveViewHolder(private val binding: ItemRiwayatCutiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false

        fun bind(leave: Leave) {
            binding.tvCutiRange.text = "${leave.leave_date}"
            binding.tvCutiType.text = "Tipe Cuti: ${leave.type_name}"
            binding.tvAlasanCuti.text = leave.reason
            binding.tvStatus.text = leave.status

            binding.layoutAlasanCuti.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandIcon.setImageResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more)

            binding.expandIcon.setOnClickListener {
                isExpanded = !isExpanded

                binding.expandIcon.setImageResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more)

                val anim = AnimationUtils.loadAnimation(
                    binding.root.context,
                    if (isExpanded) R.anim.anim_rotate_expand else R.anim.anim_rotate_collapse
                )
                binding.expandIcon.startAnimation(anim)

                binding.layoutAlasanCuti.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }
        }
    }
}

