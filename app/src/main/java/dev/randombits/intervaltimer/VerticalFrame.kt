package dev.randombits.intervaltimer

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup

/**
 * Stands its only child on end, with its start at the bottom, so that a stock SeekBar works as a
 * vertical slider and keeps the platform's touch, keyboard and accessibility handling.
 */
class VerticalFrame(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    override fun onViewAdded(child: View) {
        super.onViewAdded(child);
        // rotating left-to-right content puts its start at the bottom, also in RTL locales
        child.layoutDirection = View.LAYOUT_DIRECTION_LTR;
        child.rotation = 270f;
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = getChildAt(0);
        child.measure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(
            resolveSize(child.measuredHeight, widthMeasureSpec),
            resolveSize(child.measuredWidth, heightMeasureSpec)
        );
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val child = getChildAt(0);
        // centered on this frame, so that rotating around its own center keeps it within the frame
        val childLeft = (right - left - child.measuredWidth) / 2;
        val childTop = (bottom - top - child.measuredHeight) / 2;
        child.layout(childLeft, childTop, childLeft + child.measuredWidth, childTop + child.measuredHeight);
    }

    // this doesn't scroll, so the slider can start dragging right away
    override fun shouldDelayChildPressedState() = false;

    // the arrow keys turn with the child, so that up and down move the slider
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> KeyEvent.KEYCODE_DPAD_RIGHT
            KeyEvent.KEYCODE_DPAD_RIGHT -> KeyEvent.KEYCODE_DPAD_DOWN
            KeyEvent.KEYCODE_DPAD_DOWN -> KeyEvent.KEYCODE_DPAD_LEFT
            KeyEvent.KEYCODE_DPAD_LEFT -> KeyEvent.KEYCODE_DPAD_UP
            else -> return super.dispatchKeyEvent(event)
        };
        return super.dispatchKeyEvent(KeyEvent(
            event.downTime, event.eventTime, event.action, keyCode, event.repeatCount,
            event.metaState, event.deviceId, event.scanCode, event.flags, event.source
        ));
    }
}
