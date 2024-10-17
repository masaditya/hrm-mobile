package com.hrmapps.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.R
import com.hrmapps.data.model.Leave
import com.hrmapps.databinding.ItemRiwayatCutiBinding

class LeaveAdapter(private val leaveList: List<Leave>) :
    RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = ItemRiwayatCutiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        val leave = leaveList[position]
        holder.bind(leave)
    }

    override fun getItemCount() = leaveList.size

    inner class LeaveViewHolder(private val binding: ItemRiwayatCutiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false

        fun bind(leave: Leave) {
            binding.tvCutiRange.text = "${leave.tanggalCuti} - ${leave.tanggalMasuk}"
            binding.tvCutiType.text = "Tipe Cuti: ${leave.typeCuti}"
            binding.tvAlasanCuti.text = leave.alasan

            binding.layoutAlasanCuti.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.expandIcon.setImageResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more)

            binding.expandIcon.setOnClickListener {
                isExpanded = !isExpanded
                val anim = AnimationUtils.loadAnimation(
                    binding.root.context,
                    if (isExpanded) R.anim.anim_rotate_expand else R.anim.anim_rotate_collapse
                )
                binding.expandIcon.startAnimation(anim)

                binding.layoutAlasanCuti.visibility = if (isExpanded) View.VISIBLE else View.GONE
            }

            binding.root.setOnClickListener {
                binding.expandIcon.performClick()
            }
        }
    }
}