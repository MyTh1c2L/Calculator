package com.example.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Button button = new Button(this);
        button.setText("Назад");
        button.setOnClickListener(view -> backToMain(view));

        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("graph.db", MODE_PRIVATE, null);

        DrawView view = new DrawView(this, db);

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

    SQLiteDatabase db;

    public DrawView(Context context, SQLiteDatabase db) {
        super(context);
        paint = new Paint();
        paintGrid = new Paint();
        this.db = db;
        createTableAndClean();
    }

    private void createTableAndClean() {
        db.execSQL("CREATE TABLE IF NOT EXISTS points (x REAL, y REAL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS min_max (x REAL, y REAL)");
        db.execSQL("DELETE FROM points");
        db.execSQL("DELETE FROM min_max");
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

        float x = 0;
        List<PointF> points = new ArrayList<>();
        Path path = new Path();
        path.moveTo(0, 200);
        while (true) {
            PointF p1 = new PointF((x * 20), (float) (200 - 50 * Math.sin(x)));
            x += Math.PI / 10;
            PointF p2 = new PointF((x * 20), (float) (200 - 50 * Math.sin(x)));
            x += Math.PI / 10;
            path.quadTo(p1.x, p1.y, p2.x, p2.y);

            points.add(p1);
            points.add(p2);

            if(p1.x > maxSizeX || p2.x > maxSizeX)
                break;
        }
        canvas.drawPath(path, paint);

        saveToDB(points, "points");

        Paint paintPoints = new Paint();
        paintPoints.setColor(Color.RED);
        paintPoints.setStrokeWidth(10);
        List<PointF> pointsMinMax = getMinMaxPoints();
        for (PointF point : pointsMinMax) {
            canvas.drawPoint(point.x, point.y, paintPoints);
        }
    }

    private void saveToDB(List<PointF> points, String tableName) {
        for (PointF point : points) {
            ContentValues cv = new ContentValues();
            cv.put("x", point.x);
            cv.put("y", point.y);

            db.insert(tableName, null, cv);
        }
    }

    private List<PointF> getMinMaxPoints() {
        Cursor res = db.rawQuery("SELECT * FROM points", null);
        List<PointF> points = new ArrayList<>();

        res.moveToFirst();
        while(res.isAfterLast() == false) {
           PointF point = new PointF();
           point.x = res.getFloat(0);
           point.y = res.getFloat(1);

           points.add(point);
           res.moveToNext();
        }

        List<PointF> minMaxPoint = new ArrayList<>();
        float maxY = points.get(0).y;
        float minY = points.get(0).y;
        for (PointF point : points) {
            if (point.y > maxY)
                maxY = point.y;

            if (point.y < minY)
                minY = point.y;
        }

        for (PointF point : points) {
            if (point.y == maxY || point.y == minY)
                minMaxPoint.add(point);
        }

        saveToDB(minMaxPoint, "min_max");
        return minMaxPoint;
    }
}
