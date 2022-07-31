package com.example.tracingapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tracingapp.databinding.ItemsHistoryBinding
import com.example.tracingapp.data.model.History

class HistoryAdapter: ListAdapter<History, HistoryAdapter.HistoryViewHolder>(HISTORY_COMPARATOR) {

    class HistoryViewHolder(val binding: ItemsHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.tvCheckinLocation.text = history.location
            binding.tvCheckinDate.text = history.date
            binding.tvCheckinTime.text = history.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemsHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val HISTORY_COMPARATOR = object: DiffUtil.ItemCallback<History>() {
            override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem.location == newItem.location && oldItem.date == newItem.date
            }
        }
    }
}