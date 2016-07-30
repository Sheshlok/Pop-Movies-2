package in.uncod.android.bypass.style;

import android.content.res.ColorStateList;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 *  Created by sheshloksamal on 29/07/16.
 *
 * An extension to URLSpan which changes it's background & foreground color when clicked.
 *
 * Derived from http://stackoverflow.com/a/20905824.
 */
public class TouchableUrlSpan extends URLSpan {

    private static int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    private boolean isPressed;
    private int normalTextColor;
    private int pressedTextColor;
    private int pressedBackgroundColor;

    public TouchableUrlSpan(String url,
                            ColorStateList textColor,
                            int pressedBackgroundColor) {
        super(url);
        this.normalTextColor = textColor.getDefaultColor();
        this.pressedTextColor = textColor.getColorForState(STATE_PRESSED, normalTextColor);
        this.pressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    @Override
    public void updateDrawState(TextPaint drawState) {
        drawState.setColor(isPressed ? pressedTextColor : normalTextColor);
        drawState.bgColor = isPressed ? pressedBackgroundColor : 0;
        drawState.setUnderlineText(!isPressed);
    }
}