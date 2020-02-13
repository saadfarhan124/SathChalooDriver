package com.example.sathchaloodriver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sathchaloodriver.R

class HistoryAdapter: RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = LayoutInflater.inflate(R.layout.activity_history_row, parent, false)
        return HistoryViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

    }
}
class HistoryViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}