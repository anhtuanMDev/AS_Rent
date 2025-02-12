package com.example.rentalmanagement.customs

import android.content.Context
import android.util.AttributeSet

class NonClickableSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatSpinner(context, attrs, defStyle) {
    var isSpinnerClickable: Boolean = true

    override fun performClick(): Boolean {
        return if (isSpinnerClickable) super.performClick() else false
    }
}