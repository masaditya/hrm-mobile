package com.hrmapps.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.R
import com.hrmapps.data.model.leave.Leave
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

                binding.expandIcon.setImageResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more)

                val anim = AnimationUtils.loadAnimation(
                    binding.root.context,
                    if (isExpanded) R.anim.anim_rotate_expand else R.anim.anim_rotate_collapse
                )
                binding.expandIcon.startAnimation(anim)

                if (isExpanded) {
                    expand(binding.layoutAlasanCuti)
                } else {
                    collapse(binding.layoutAlasanCuti)
                }
            }

            binding.root.setOnClickListener {
                binding.expandIcon.performClick()
            }
        }

        private fun expand(view: View) {
            view.visibility = View.VISIBLE
            view.measure(View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            view.alpha = 0f
            view.scaleY = 0.0f
            view.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .withStartAction { view.layoutParams.height = 0; view.requestLayout() }
                .withEndAction { view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT; view.requestLayout() }
                .start()
        }

        private fun collapse(view: View) {
            val initialHeight = view.measuredHeight

            view.animate()
                .alpha(0f)
                .scaleY(0f)
                .setDuration(200)
                .withEndAction {
                    view.visibility = View.GONE
                    view.layoutParams.height = initialHeight
                }
                .start()
        }
    }

}
