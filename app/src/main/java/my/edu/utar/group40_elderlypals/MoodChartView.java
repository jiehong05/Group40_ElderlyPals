package my.edu.utar.group40_elderlypals;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MoodChartView extends View {

    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private List<Integer> moodValues = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();
    private List<Point> pointCoords = new ArrayList<>();
    
    private OnPointSelectedListener listener;

    public interface OnPointSelectedListener {
        void onPointLongPressed(int index);
    }

    public void setOnPointSelectedListener(OnPointSelectedListener listener) {
        this.listener = listener;
    }

    public MoodChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);
        textPaint.setAntiAlias(true);
    }

    public void setData(List<Integer> values, List<String> times) {
        this.moodValues = values;
        this.timestamps = times;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float tx = event.getX();
            float ty = event.getY();
            
            for (int i = 0; i < pointCoords.size(); i++) {
                Point p = pointCoords.get(i);
                // Check if touch is near a point (30px radius)
                if (Math.abs(p.x - tx) < 40 && Math.abs(p.y - ty) < 40) {
                    if (listener != null) {
                        listener.onPointLongPressed(i);
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (moodValues == null || moodValues.isEmpty()) {
            pointCoords.clear();
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int padding = 60;
        int chartWidth = width - (2 * padding);
        int chartHeight = height - (2 * padding);

        pointCoords.clear();

        // Draw Y Axis Labels
        String[] labels = {"", "Tired", "Sad", "Neutral", "Happy"};
        for (int i = 1; i <= 4; i++) {
            float y = padding + chartHeight - ((i - 1) * chartHeight / 3f);
            canvas.drawText(labels[i], 10, y, textPaint);
        }

        if (moodValues.size() < 2) {
            float x = padding;
            float y = padding + chartHeight - ((moodValues.get(0) - 1) * chartHeight / 3f);
            canvas.drawCircle(x, y, 15, pointPaint);
            pointCoords.add(new Point(x, y));
            return;
        }

        float stepX = (float) chartWidth / (moodValues.size() - 1);

        for (int i = 0; i < moodValues.size(); i++) {
            float x = padding + (i * stepX);
            float y = padding + chartHeight - ((moodValues.get(i) - 1) * chartHeight / 3f);

            canvas.drawCircle(x, y, 15, pointPaint);
            pointCoords.add(new Point(x, y));

            if (i > 0) {
                float prevX = padding + ((i - 1) * stepX);
                float prevY = padding + chartHeight - ((moodValues.get(i - 1) - 1) * chartHeight / 3f);
                canvas.drawLine(prevX, prevY, x, y, linePaint);
            }
            
            if (i % 2 == 0 || i == moodValues.size() - 1) {
                 canvas.drawText(timestamps.get(i), x - 20, height - 10, textPaint);
            }
        }
    }

    private static class Point {
        float x, y;
        Point(float x, float y) { this.x = x; this.y = y; }
    }
}
