package com.example.colorpicker

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ColorAdapter(
    private val colorList: MutableList<ColorItem>,
    private val listener: ColorItemListener,
    private val onItemClick: (ColorItem) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    class ColorViewHolder(itemView: View, private val listener: ColorItemListener, private val colorList: List<ColorItem>, private val onItemClick: (ColorItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val colorView: View = itemView.findViewById(R.id.color_view_id)
        val hexCodeText: TextView = itemView.findViewById(R.id.color_code_id)
        private val deleteIcon: ImageView = itemView.findViewById(R.id.delete_item_id)
        private val copyIcon: ImageButton = itemView.findViewById(R.id.copy_color_id)

        init {
            deleteIcon.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }

            copyIcon.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCopyClick(colorList[position].hexCode)
                }
            }

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(colorList[position])
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.selected_color_item, parent, false)
        return ColorViewHolder(view, listener, colorList, onItemClick)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {

        val shape = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setStroke(1, Color.GRAY)
            setColor(colorList[position].colorInt)
        }
        holder.colorView.background = shape
        holder.hexCodeText.text = colorList[position].hexCode

    }

    override fun getItemCount() = colorList.size

    fun addColor(colorInt: Int, hexCode: String) {
        colorList.add(0, ColorItem(colorInt, hexCode))
        notifyItemInserted(0)
    }

    fun removeColor(position: Int) {
        colorList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getColors(): List<ColorItem> = colorList
}

interface ColorItemListener {
    fun onDeleteClick(position: Int)
    fun onCopyClick(hexCode: String)
}