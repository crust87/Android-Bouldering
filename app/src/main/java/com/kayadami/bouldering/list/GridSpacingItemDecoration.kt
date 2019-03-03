package com.kayadami.bouldering.list

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View

/**
 * @param spanCount 단수
 */
class GridSpacingItemDecoration(var spanCount: Int) : RecyclerView.ItemDecoration() {
    var spacing = 0
    var hasHeader = false

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spanIndex = (view.layoutParams as StaggeredGridLayoutManager.LayoutParams).spanIndex
        val position = parent.getChildAdapterPosition(view)

        if (hasHeader && position == 0) {
            outRect.left = 0
            outRect.right = 0
            outRect.bottom = 0
        } else {
            outRect.left = spacing / spanCount * (spanCount - spanIndex)
            outRect.right = spacing / spanCount * (spanIndex + 1)
            outRect.bottom = spacing
        }
    }
}