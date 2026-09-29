/**
 * Created by Karim Mreisi.
 */

package com.limelight.binding.input.virtual_controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class DigitalPad extends VirtualControllerElement {
    public final static int DIGITAL_PAD_DIRECTION_NO_DIRECTION = 0;
    int direction = DIGITAL_PAD_DIRECTION_NO_DIRECTION;
    public final static int DIGITAL_PAD_DIRECTION_LEFT = 1;
    public final static int DIGITAL_PAD_DIRECTION_UP = 2;
    public final static int DIGITAL_PAD_DIRECTION_RIGHT = 4;
    public final static int DIGITAL_PAD_DIRECTION_DOWN = 8;
    List<DigitalPadListener> listeners = new ArrayList<>();

    private static final int DPAD_MARGIN = 5;

    private final Paint paint = new Paint();

    public DigitalPad(VirtualController controller, Context context) {
        super(controller, context, EID_DPAD);
    }

    public void addDigitalPadListener(DigitalPadListener listener) {
        listeners.add(listener);
    }

    @Override
protected void onElementDraw(Canvas canvas) {
    canvas.drawColor(Color.TRANSPARENT);

    float w = getWidth();
    float h = getHeight();

    float cx = w / 2f;
    float cy = h / 2f;

    float buttonW = w * 0.30f;
    float buttonH = h * 0.30f;
    float gap = w * 0.035f;

    paint.setStrokeWidth(getDefaultStrokeWidth());

    // ESQUERDA
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.argb(
            (direction & DIGITAL_PAD_DIRECTION_LEFT) != 0 ? 190 : 120,
            0, 0, 0));
    canvas.drawOval(
            cx - gap - buttonW,
            cy - buttonH / 2f,
            cx - gap,
            cy + buttonH / 2f,
            paint);

    // DIREITA
    paint.setColor(Color.argb(
            (direction & DIGITAL_PAD_DIRECTION_RIGHT) != 0 ? 190 : 120,
            0, 0, 0));
    canvas.drawOval(
            cx + gap,
            cy - buttonH / 2f,
            cx + gap + buttonW,
            cy + buttonH / 2f,
            paint);

    // CIMA
    paint.setColor(Color.argb(
            (direction & DIGITAL_PAD_DIRECTION_UP) != 0 ? 190 : 120,
            0, 0, 0));
    canvas.drawOval(
            cx - buttonW / 2f,
            cy - gap - buttonH,
            cx + buttonW / 2f,
            cy - gap,
            paint);

    // BAIXO
    paint.setColor(Color.argb(
            (direction & DIGITAL_PAD_DIRECTION_DOWN) != 0 ? 190 : 120,
            0, 0, 0));
    canvas.drawOval(
            cx - buttonW / 2f,
            cy + gap,
            cx + buttonW / 2f,
            cy + gap + buttonH,
            paint);

    // Setas brancas
    paint.setColor(Color.WHITE);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(getPercent(getCorrectWidth(), 18));
    paint.setStyle(Paint.Style.FILL);

    canvas.drawText("◀", cx - gap - buttonW / 2f,
            cy + paint.getTextSize() / 3f, paint);

    canvas.drawText("▶", cx + gap + buttonW / 2f,
            cy + paint.getTextSize() / 3f, paint);

    canvas.drawText("▲", cx,
            cy - gap - buttonH / 2f + paint.getTextSize() / 3f, paint);

    canvas.drawText("▼", cx,
            cy + gap + buttonH / 2f + paint.getTextSize() / 3f, paint);
}

    private void newDirectionCallback(int direction) {
        _DBG("direction: " + direction);

        // notify listeners
        for (DigitalPadListener listener : listeners) {
            listener.onDirectionChange(direction);
        }
    }

    @Override
    public boolean onElementTouchEvent(MotionEvent event) {
        // get masked (not specific to a pointer) action
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                direction = 0;

                if (event.getX() < getPercent(getWidth(), 33)) {
                    direction |= DIGITAL_PAD_DIRECTION_LEFT;
                }
                if (event.getX() > getPercent(getWidth(), 66)) {
                    direction |= DIGITAL_PAD_DIRECTION_RIGHT;
                }
                if (event.getY() > getPercent(getHeight(), 66)) {
                    direction |= DIGITAL_PAD_DIRECTION_DOWN;
                }
                if (event.getY() < getPercent(getHeight(), 33)) {
                    direction |= DIGITAL_PAD_DIRECTION_UP;
                }
                newDirectionCallback(direction);
                invalidate();

                return true;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                direction = 0;
                newDirectionCallback(direction);
                invalidate();

                return true;
            }
            default: {
            }
        }

        return true;
    }

    public interface DigitalPadListener {
        void onDirectionChange(int direction);
    }
}
