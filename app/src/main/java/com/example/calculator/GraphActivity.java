package com.example.calculator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button button = new Button(this);
        button.setText("Назад");
        button.setOnClickListener(view -> backToMain(view));

        DrawView view = new DrawView(this);

        layout.addView(button,0);
        layout.addView(view,1);

        setContentView(layout);
    }

    public void backToMain(View view) {
        this.finish();
    }
}

class DrawView extends View {

    Paint paint;
    Paint paintGrid;

    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        paintGrid = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255,255,255,255);

        int maxSizeX = canvas.getWidth();
        int maxSizeY = canvas.getHeight();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(50);
        paint.setPathEffect(new CornerPathEffect(50));

        paintGrid.setStrokeWidth(2);
        paintGrid.setColor(Color.GRAY);
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setPathEffect(new DashPathEffect(new float[]{ 5, 5 }, 0));

        int X = 0;
        for(int i = 0; i < maxSizeX / 30; i++) {
            canvas.drawLine(X, 0, X, maxSizeY, paintGrid);
            X += 30;
        }

        int Y = 0;
        for(int i = 0; i < maxSizeY / 30; i++) {
            canvas.drawLine(0, Y, maxSizeX, Y, paintGrid);
            Y += 30;
        }

        Path path = new Path();
        path.moveTo(0, 300);
        for (int i = 0; i < maxSizeX; i += 180) {
            path.rQuadTo(45, 100, 90, 0);
            path.rQuadTo(45, -100, 90, 0);
        }
        canvas.drawText("rQuadTo", 25, 200, paint);
        canvas.drawPath(path, paint);

        path = new Path();
        path.moveTo(0, 600);
        for (float x = (float) (Math.PI / 4); x * 30 < maxSizeX; x += (Math.PI / 4)) {
            float y = (float) Math.sin(x);
            path.lineTo(x * 30, 600 + y * 100 );
        }
        canvas.drawText("lineTo", 25, 500, paint);
        canvas.drawPath(path, paint);
    }
}
