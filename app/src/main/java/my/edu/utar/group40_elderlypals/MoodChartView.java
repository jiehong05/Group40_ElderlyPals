package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MoodChartView extends View {

    public interface OnPointSelectedListener {
        void onPointSelected(int index);
    }

    private final List<Integer> moodValues = new ArrayList<>();
    private final List<String> timestamps = new ArrayList<>();
    private final List<Float> pointX = new ArrayList<>();
    private final List<Float> pointY = new ArrayList<>();

    private OnPointSelectedListener listener;

    private Paint cardBgPaint;
    private Paint gridPaint;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint labelPaint;
    private Paint timePaint;

    public MoodChartView(Context context) {
        super(context);
        init();
    }

    public MoodChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoodChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cardBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cardBgPaint.setColor(Color.WHITE);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E8E6F0"));
        gridPaint.setStrokeWidth(dp(1));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#6A4FC3"));
        linePaint.setStrokeWidth(dp(3));
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#6A4FC3"));
        pointPaint.setStyle(Paint.Style.FILL);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(Color.parseColor("#6A4FC3"));
        labelPaint.setTextSize(sp(14));
        labelPaint.setTextAlign(Paint.Align.LEFT);

        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(Color.parseColor("#8A8699"));
        timePaint.setTextSize(sp(11));
        timePaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<Integer> values, List<String> times) {
        moodValues.clear();
        timestamps.clear();

        if (values != null) {
            moodValues.addAll(values);
        }
        if (times != null) {
            timestamps.addAll(times);
        }

        invalidate();
    }

    public void setOnPointSelectedListener(OnPointSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float leftPad = dp(44);
        float rightPad = dp(16);
        float topPad = dp(18);
        float bottomPad = dp(30);

        float chartLeft = leftPad;
        float chartRight = w - rightPad;
        float chartTop = topPad;
        float chartBottom = h - bottomPad;

        float chartHeight = chartBottom - chartTop;
        float rowGap = chartHeight / 3f;

        float happyY = chartTop;
        float neutralY = chartTop + rowGap;
        float sadY = chartTop + rowGap * 2;
        float tiredY = chartBottom;

        // Horizontal guide lines
        canvas.drawLine(chartLeft, happyY, chartRight, happyY, gridPaint);
        canvas.drawLine(chartLeft, neutralY, chartRight, neutralY, gridPaint);
        canvas.drawLine(chartLeft, sadY, chartRight, sadY, gridPaint);
        canvas.drawLine(chartLeft, tiredY, chartRight, tiredY, gridPaint);

        // Mood labels on left
        canvas.drawText("Happy", dp(4), happyY + dp(4), labelPaint);
        canvas.drawText("Neutral", dp(2), neutralY + dp(4), labelPaint);
        canvas.drawText("Sad", dp(12), sadY + dp(4), labelPaint);
        canvas.drawText("Tired", dp(8), tiredY + dp(4), labelPaint);

        pointX.clear();
        pointY.clear();

        if (moodValues.isEmpty()) {
            return;
        }

        float stepX;
        if (moodValues.size() == 1) {
            stepX = 0;
        } else {
            stepX = (chartRight - chartLeft) / (moodValues.size() - 1);
        }

        Path path = new Path();

        for (int i = 0; i < moodValues.size(); i++) {
            float x = chartLeft + (i * stepX);
            float y = mapMoodToY(moodValues.get(i), happyY, neutralY, sadY, tiredY);

            pointX.add(x);
            pointY.add(y);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        canvas.drawPath(path, linePaint);

        for (int i = 0; i < pointX.size(); i++) {
            canvas.drawCircle(pointX.get(i), pointY.get(i), dp(5), pointPaint);

            if (i < timestamps.size()) {
                canvas.drawText(timestamps.get(i), pointX.get(i), h - dp(8), timePaint);
            }
        }
    }

    private float mapMoodToY(int mood, float happyY, float neutralY, float sadY, float tiredY) {
        switch (mood) {
            case 4:
                return happyY;
            case 3:
                return neutralY;
            case 2:
                return sadY;
            case 1:
            default:
                return tiredY;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && listener != null) {
            float touchX = event.getX();
            float touchY = event.getY();

            for (int i = 0; i < pointX.size(); i++) {
                float dx = touchX - pointX.get(i);
                float dy = touchY - pointY.get(i);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance <= dp(18)) {
                    listener.onPointSelected(i);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}