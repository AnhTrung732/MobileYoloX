package com.example.mobileyolox.CustomImageView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class CustomImageView extends androidx.appcompat.widget.AppCompatImageView {
    private List<Pair<RectF,String>> mRectangles = new ArrayList<>();


    public CustomImageView(Context context) {
        super(context);
        init();
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Configure your paint object here
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        //        paint.setStyle(Paint.Style.FILL);
    }

    public void setRectangles(List<Pair<RectF, String>> rectangles) {
        mRectangles = rectangles;
        invalidate(); // Redraw the view when the rectangle list is updated
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);

        Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setTextSize(35);

        for (Pair<RectF, String> rectangle : mRectangles)
        {
            canvas.drawRect(rectangle.first, paint);
            canvas.drawText(rectangle.second, rectangle.first.left, rectangle.first.top, paint2);
        }
    }
}
