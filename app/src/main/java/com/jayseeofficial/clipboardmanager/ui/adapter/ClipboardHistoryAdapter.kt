package com.jayseeofficial.clipboardmanager.ui.adapter

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import com.jayseeofficial.clipboardmanager.data.ClipboardData
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.jayseeofficial.clipboardmanager.R
import kotlinx.android.synthetic.main.item_clipboard_data.view.*
import java.util.*

class ClipboardHistoryAdapter(clipboardHistory: LiveData<List<ClipboardData>>, lifecycleOwner: LifecycleOwner) : RecyclerView.Adapter<ClipboardHistoryAdapter.ClipboardItemViewHolder>() {
    private val TAG = ClipboardHistoryAdapter::class.java.simpleName

    init {
        clipboardHistory.observe(lifecycleOwner, Observer {
            // If the history is null, just provide an empty history
            history = it ?: listOf()
            // Make it newest to oldest instead of oldest to newest
            Collections.reverse(history)
            notifyDataSetChanged()
            Log.d(TAG, "History updated: " + Gson().toJson(it))
        })
    }

    private var history: List<ClipboardData> = listOf()
    private var listener: ((ClipboardData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClipboardItemViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_clipboard_data, parent, false)
        return ClipboardItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClipboardItemViewHolder?, position: Int) {
        holder?.bind(getItemAt(position), listener)
    }

    override fun getItemCount(): Int {
        return history.size
    }

    private fun getItemAt(i: Int): ClipboardData {
        return history[i]
    }

    fun setItemClickedListener(l: (ClipboardData) -> Unit) {
        listener = l
    }

    fun unsetItemClickedListener() {
        listener = null
    }

    class ClipboardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var txtClipText = itemView.txtClipboardText
        private var txtTimeSince = itemView.txtTimeSince

        fun bind(cd: ClipboardData, l: ((ClipboardData) -> Unit)?) {
            txtClipText.text = cd.text
            txtTimeSince.text = DateUtils.getRelativeDateTimeString(itemView.context, cd.timestamp, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0)
            itemView.setOnClickListener { l?.invoke(cd) }
        }
    }


}