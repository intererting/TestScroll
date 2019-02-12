package com.yuliyang.testscroll

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.TYPE_TOUCH

const val DIVIDER = 1000
const val FACTOR = 0.4f

class NestedScrollTest @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {


    private lateinit var topView: View
    private var totalScroll: Int = 0
    private var openMode = false
    private val childView by lazy {
        val childView = View.inflate(context, R.layout.child_layout, null)
        childView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1920)
        childView.visibility = View.GONE
        childView
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        addView(childView, 0)
        topView = getChildAt(1)
    }

    private val helper = NestedScrollingParentHelper(this)

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        println("onNestedScrollAccepted")
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, TYPE_TOUCH)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        return onStartNestedScroll(child, target, axes, TYPE_TOUCH)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return true
    }

    override fun getNestedScrollAxes(): Int {
        return helper.nestedScrollAxes
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        //滚动之前
        if (openMode || target.canScrollVertically(1)) {
            totalScroll += dy
        }
        if (totalScroll in -DIVIDER..0) {
            consumed[1] = Math.abs(totalScroll)
        }
        if (openMode && totalScroll > 0) {
            consumed[1] = totalScroll
            this.translationY = totalScroll * (-FACTOR)
        }
        if (totalScroll <= 0 && !openMode) {
            topView.setPadding(0, (Math.abs(totalScroll) * FACTOR).toInt(), 0, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
            if (getChildAt(i).visibility == View.GONE) {
                continue
            }
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }
        var totalHeight = 0
        for (i in 0 until childCount) {
            if (getChildAt(i).visibility == View.GONE) {
                continue
            }
            totalHeight += getChildAt(i).measuredHeight
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), totalHeight)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, TYPE_TOUCH)
    }

    override fun onNestedScroll(
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int,
            consumed: IntArray
    ) {
    }

    override fun onNestedScroll(
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int
    ) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, TYPE_TOUCH, intArrayOf())
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, TYPE_TOUCH)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (openMode) {
            if (totalScroll > DIVIDER) {
                childView.visibility = View.GONE
                openMode = false
            }
            this.translationY = 0f
            totalScroll = 0
        } else {
            if (totalScroll < 0) {
                if (totalScroll < -DIVIDER) {
                    childView.visibility = View.VISIBLE
                    openMode = true
                }
                totalScroll = 0
                topView.setPadding(0, 0, 0, 0)
            }
        }
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, TYPE_TOUCH)
    }
}