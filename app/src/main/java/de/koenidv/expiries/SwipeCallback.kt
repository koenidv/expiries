package de.koenidv.expiries

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors


abstract class SwipeCallback(val context: Context) : ItemTouchHelper.Callback() {

    private val paint = Paint()
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_check)

    init {
        paint.color = MaterialColors.getColor(context, android.R.attr.colorSecondary, Color.GREEN)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is ExpiryItemAdapter.ArticleViewHolder)
            makeMovementFlags(0, ItemTouchHelper.RIGHT)
        else 0
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView

        c.drawRoundRect(
            itemView.left + 8.toPx,
            itemView.top + 8.toPx,
            itemView.left + dX + 32.toPx,
            itemView.bottom.toFloat(),
            8.toPx,
            8.toPx,
            paint
        )

        val iconWidth = 32.toPx
        val remainingHeight = itemView.height - 8.toPx - iconWidth
        icon?.setBounds(
            itemView.left + 24.toPx.toInt(),
            itemView.top + (8.toPx + remainingHeight / 2).toInt(),
            itemView.left + (24.toPx + iconWidth).toInt(),
            itemView.bottom - (remainingHeight / 2).toInt()
        )
        icon?.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    private val Number.toPx
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            Resources.getSystem().displayMetrics
        )
}