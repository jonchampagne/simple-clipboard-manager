/*
 * The MIT License (MIT) (Modified)
 *
 * Copyright (c) 2017 Jon Champagne
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * The copyright owner is notified when the Software is published, distributed, sublicensed, and/or
 * copies are sold.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.jayseeofficial.clipboardmanager.ui.adapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.support.v4.app.SupportActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import com.jayseeofficial.clipboardmanager.data.ClipboardData
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.jayseeofficial.clipboardmanager.R
import com.jayseeofficial.clipboardmanager.data.ClipboardHistoryDatabase
import kotlinx.android.synthetic.main.item_clipboard_data.view.*
import java.util.*
import kotlin.concurrent.thread

class ClipboardHistoryAdapter(clipboardHistory: LiveData<List<ClipboardData>>, private val activity: SupportActivity) : RecyclerView.Adapter<ClipboardHistoryAdapter.ClipboardItemViewHolder>() {
    private val TAG = ClipboardHistoryAdapter::class.java.simpleName

    init {
        clipboardHistory.observe(activity, Observer {
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
        var clipboardData: ClipboardData? = null

        private var txtClipText = itemView.txtClipboardText
        private var txtTimeSince = itemView.txtTimeSince

        fun bind(cd: ClipboardData, l: ((ClipboardData) -> Unit)?) {
            clipboardData = cd
            txtClipText.text = cd.text
            txtTimeSince.text = DateUtils.getRelativeDateTimeString(itemView.context, cd.timestamp, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0)
            itemView.setOnClickListener { l?.invoke(cd) }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView != null) {
            print("Attached to recyclerview")

            val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    thread {
                        if (viewHolder is ClipboardItemViewHolder) {
                            Log.d(TAG, "Item swiped!")
                            val chd = ClipboardHistoryDatabase.get(viewHolder.itemView.context).clipboardHistoryDao()
                            chd.deleteClipboardData(viewHolder.clipboardData!!)

                            activity.runOnUiThread { notifyItemRemoved(viewHolder.adapterPosition) }
                        }
                    }

                }
            }

            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

            itemTouchHelper.attachToRecyclerView(recyclerView)
        }
    }
}