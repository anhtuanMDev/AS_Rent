package com.example.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.rentalmanagement.R

class StepBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentStep: Int = 0
    private var steps: Int = 5
    private var canGoUpTo: Int = 0 // Store the highest active step
    private val barColor: Int
    private val mockColor: Int
    private val inactiveBarColor: Int
    private val inactiveMockColor: Int
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 36f // Adjust as needed
    }

    private val circleRadius = 40f // Circle radius in pixels
    private val padding = circleRadius * 2 // Padding for mocks

    // List to store the center X positions of all circles
    private val mockCenters = mutableListOf<Float>()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.StepBar, 0, 0).apply {
            try {
                steps = getInteger(R.styleable.StepBar_stepCount, steps)
                currentStep = getInteger(R.styleable.StepBar_currentStep, currentStep)
                canGoUpTo = getInteger(R.styleable.StepBar_canGoUpTo, canGoUpTo) // Add canGoUpTo
                barColor = getColor(R.styleable.StepBar_barColor, Color.GREEN)
                mockColor = getColor(R.styleable.StepBar_mockColor, Color.BLUE)
                inactiveBarColor = getColor(R.styleable.StepBar_inactiveBarColor, Color.GRAY)
                inactiveMockColor = getColor(R.styleable.StepBar_inactiveMockColor, Color.DKGRAY)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mockCenters.clear()
        val availableWidth = width - padding
        val spacing = availableWidth / (steps - 1)

        // Draw the connecting bar
        for (i in 0 until steps - 1) {
            paint.color = if (i < currentStep - 1) barColor else inactiveBarColor
            val startX = padding / 2 + i * spacing
            val endX = startX + spacing
            val centerY = height / 2f
            canvas.drawRect(startX, centerY - 10f, endX, centerY + 10f, paint)
        }

        // Draw the mock circles
        for (i in 0 until steps) {
            paint.color = if (i < canGoUpTo) { // Check if mock is within allowed range
                if (i < currentStep) mockColor else inactiveMockColor
            } else {
                inactiveMockColor // Mock is inactive if index is larger than canGoUpTo
            }

            val cx = padding / 2 + i * spacing
            val cy = height / 2f
            canvas.drawCircle(cx, cy, circleRadius, paint)

            // Draw the text inside the circle
            val text = (i + 1).toString()
            canvas.drawText(text, cx, cy + textPaint.textSize / 3, textPaint)

            // Store the center X positions of the circles
            mockCenters.add(cx)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Detect if a mock circle is touched
                val x = event.x
                val y = event.y

                for (i in mockCenters.indices) {
                    val cx = mockCenters[i]
                    val cy = height / 2f
                    if (isInsideCircle(x, y, cx, cy, circleRadius)) {
                        // Only update currentStep if the mock index is <= canGoUpTo
                        if (i < canGoUpTo) {
                            currentStep = i + 1
                            invalidate() // Redraw the view
                            performClick() // Optional: call for accessibility
                            return true
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isInsideCircle(x: Float, y: Float, cx: Float, cy: Float, radius: Float): Boolean {
        val dx = x - cx
        val dy = y - cy
        return dx * dx + dy * dy <= radius * radius
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    // Function to update canGoUpTo dynamically if needed
    fun setCanGoUpTo(step: Int) {
        canGoUpTo = step.coerceIn(0, steps - 1) // Ensure it's within bounds
        invalidate() // Redraw to reflect the changes
    }
}
