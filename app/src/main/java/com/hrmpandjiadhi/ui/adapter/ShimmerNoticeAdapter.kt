package com.hrmpandjiadhi.ui.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrmpandjiadhi.data.model.notice.Notice
import com.hrmpandjiadhi.databinding.ShimmerListNoticeBinding

class ShimmerNoticeAdapter(
    private var noticeList: List<Notice>
) : RecyclerView.Adapter<ShimmerNoticeAdapter.NoticeViewHolder>() {

    fun updateData(newNotices: List<Notice>) {
        this.noticeList = newNotices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ShimmerListNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice = noticeList[position]
        holder.bind(notice)

    }

    override fun getItemCount() = noticeList.size

    class NoticeViewHolder(private val binding: ShimmerListNoticeBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(notice: Notice) {

        }
    }
}
