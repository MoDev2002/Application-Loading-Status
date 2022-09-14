package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var progress = 0

    //Initializing custom attributes
    private var buttonColor = 0
    private var loadingColor = 0
    private var buttonTextColor = 0
    private var circleColor = 0
    private var buttonText = ""
    private var loadingText = ""

    //button text to be drawn
    private var textStr = ""

    //defining paint style
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 75f
        typeface = Typeface.DEFAULT_BOLD
    }
    private val valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(2000)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        //check when ButtonState is changed
        when(new) {
            //when the button is loading change button text and start the animation
            ButtonState.Loading -> {
                textStr = loadingText
                valueAnimator.start()
            }
            //when the button finishes loading change the text and cancel the animation and restart progress
            ButtonState.Completed -> {
                textStr = buttonText
                valueAnimator.cancel()
                progress = 0
            }
            else -> {}
        }
        invalidate()
    }


    init {
        //getting custom attributes
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            buttonText = getString(R.styleable.LoadingButton_buttonText) ?: "Download"
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: "Loading"
        }

        //Initialize animator
        valueAnimator.apply {
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        //Initial button state
        buttonState = ButtonState.Completed
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //draw Initial Button
        paint.color = buttonColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        //draw loading button
        // widthSize * progress / 360f -> to animate the color change
        paint.color = loadingColor
        canvas.drawRect(0f, 0f, widthSize.toFloat() * progress /360f, heightSize.toFloat(), paint)

        //draw text of the button
        //widthSize / 2f and heightSize / 2f to center text
        paint.color = buttonTextColor
        canvas.drawText(textStr, widthSize / 2f, heightSize / 2f + 25f, paint)

        //draw the circle
        //sweep angle = progress -> to animate the arc
        paint.color = circleColor
        canvas.drawArc(widthSize / 2f + 200f, heightSize / 2f - 50f,
            widthSize / 2f + 300f, heightSize / 2f + 50f,
            0f, progress.toFloat(), true, paint )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}