package mp.redditwalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import mp.redditwalls.databinding.ScrollFooterBinding

class LoadingStateAdapter(private val adapter: ImagesAdapter) :
    LoadStateAdapter<LoadingStateAdapter.LoadViewHolder>() {

    override fun onBindViewHolder(holder: LoadViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadViewHolder =
        LayoutInflater.from(parent.context).let {
            val binding = ScrollFooterBinding.inflate(it, parent, false)
            LoadViewHolder(binding)
        }

    inner class LoadViewHolder(private val binding: ScrollFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener {
                adapter.retry()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorMsg.isVisible =
                    !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
                errorMsg.text = (loadState as? LoadState.Error)?.error?.message
            }
        }
    }
}