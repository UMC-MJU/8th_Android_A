package com.example.flo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.tv_category)

        fun bind(category: Category, position: Int) {
            textView.text = category.name

            // 배경과 텍스트 색상 변경
            if (category.isSelected) {
                textView.setBackgroundResource(R.drawable.textview_background_select_color_radius)
                //textView.setTextColor(ContextCompat.getColor(view.context, R.color.white))
            } else {
                textView.setBackgroundResource(R.drawable.textview_background_radius)
                //textView.setTextColor(ContextCompat.getColor(view.context, R.color.gray))
            }

            // 클릭 이벤트 처리
            textView.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount(): Int = categories.size
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
}

