package com.example.weighttracker.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import com.example.weighttracker.data.model.WeightEntry;
import com.example.weighttracker.trend.DatedWeight;
import com.example.weighttracker.trend.TrendSummary;
import com.example.weighttracker.trend.WeightTrendAnalyzer;

/**
 * Hand-drawn line chart: the raw weight history plus the fitted trend line from
 * {@link WeightTrendAnalyzer}. Deliberately not a third-party charting library - the point of
 * this view is to present the regression the analyzer already computed, not to add a dependency.
 */
public class TrendChartView extends View {

    private static final float PADDING_PX = 24f;

    private final Paint seriesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<DatedWeight> series = Collections.emptyList();
    private TrendSummary summary;

    public TrendChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        seriesPaint.setColor(Color.parseColor("#1565C0"));
        seriesPaint.setStrokeWidth(4f);
        seriesPaint.setStyle(Paint.Style.STROKE);

        trendPaint.setColor(Color.parseColor("#E65100"));
        trendPaint.setStrokeWidth(5f);
        trendPaint.setStyle(Paint.Style.STROKE);
        trendPaint.setPathEffect(new DashPathEffect(new float[]{16f, 12f}, 0));

        pointPaint.setColor(Color.parseColor("#1565C0"));
        pointPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(List<WeightEntry> entries, TrendSummary summary) {
        this.series = WeightTrendAnalyzer.sortedSeries(entries);
        this.summary = summary;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (series.size() < 2 || summary == null || !summary.hasTrendLine()) {
            return;
        }

        float width = getWidth() - PADDING_PX * 2;
        float height = getHeight() - PADDING_PX * 2;
        if (width <= 0 || height <= 0) {
            return;
        }

        LocalDate firstDate = series.get(0).getDate();
        LocalDate lastDate = series.get(series.size() - 1).getDate();
        long totalDays = Math.max(1, ChronoUnit.DAYS.between(firstDate, lastDate));

        double minWeight = summary.getMinWeight();
        double weightRange = Math.max(1.0, summary.getMaxWeight() - minWeight);

        float[] xs = new float[series.size()];
        float[] ys = new float[series.size()];
        for (int i = 0; i < series.size(); i++) {
            DatedWeight point = series.get(i);
            long day = ChronoUnit.DAYS.between(firstDate, point.getDate());
            xs[i] = PADDING_PX + (float) (day / (double) totalDays) * width;
            ys[i] = PADDING_PX + (float) ((summary.getMaxWeight() - point.getWeight()) / weightRange) * height;
        }

        for (int i = 0; i < xs.length - 1; i++) {
            canvas.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1], seriesPaint);
        }
        for (int i = 0; i < xs.length; i++) {
            canvas.drawCircle(xs[i], ys[i], 6f, pointPaint);
        }

        float trendStartY = PADDING_PX + (float) ((summary.getMaxWeight() - summary.trendWeightOn(firstDate)) / weightRange) * height;
        float trendEndY = PADDING_PX + (float) ((summary.getMaxWeight() - summary.trendWeightOn(lastDate)) / weightRange) * height;
        canvas.drawLine(PADDING_PX, trendStartY, PADDING_PX + width, trendEndY, trendPaint);
    }
}
