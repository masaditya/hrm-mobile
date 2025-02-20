package com.hrmapps.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrmapps.R
import com.hrmapps.data.model.notice.Notice
import com.hrmapps.databinding.ItemNoticesBinding

class NoticeAdapter(
    private var noticeList: List<Notice>,
    private val onItemClickListener: (Notice) -> Unit
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    fun updateData(newNotices: List<Notice>) {
        this.noticeList = newNotices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]
        holder.bind(notice)

        holder.itemView.setOnClickListener {
            onItemClickListener(notice)
        }
    }

    override fun getItemCount() = noticeList.size

    class NoticeViewHolder(private val binding: ItemNoticesBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(notice: Notice) {
            binding.tvHeading.text = notice.heading
            binding.tvDateTime.text = "${notice.date} ${notice.time}"
            if (notice.read == 1) {
                binding.ivIcon.setImageResource(R.drawable.ic_notice_read)
            } else if (notice.read == 0) {
                binding.ivIcon.setImageResource(R.drawable.ic_notice_unread)
            } else {
                binding.ivIcon.setImageResource(R.drawable.bg_dialog_rounded)
                binding.ivIcon.setBackgroundColor(R.color.gray_light)
            }
        }
    }
}
