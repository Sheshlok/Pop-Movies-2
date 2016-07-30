package in.uncod.android.bypass.style;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

/**
 * Created by sheshloksamal on 29/07/16.
 *
 A simple text span used to mark text that will be replaced by an image once it has been
 * downloaded. See {@link in.uncod.android.bypass.Bypass.ImageGetter}
 */
public class ImageLoadingSpan extends CharacterStyle {
    @Override
    public void updateDrawState(TextPaint textPaint) {
        // no-op
    }
}