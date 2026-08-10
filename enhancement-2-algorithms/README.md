# Enhancement Two — Algorithms & Data Structures

A **weight trend analysis engine** that turns stored weight entries into
statistics and a goal projection. The regression is implemented by hand rather
than pulled from a library.

## What it does

- Loads weight history into a time series sorted by date.
- Computes summary statistics: current weight, total change, min, max, and a
  seven-day moving average.
- Fits a **least-squares linear regression** line over the series (single-pass
  accumulation of the regression sums) to find the rate of change.
- Projects an estimated date to reach the goal weight, with guards against the
  divide-by-zero cases (a flat trend, or a series with fewer than two points).
- Renders the history and trend line on a custom `TrendChartView`.

## Key files

- [WeightTrendAnalyzer.java](WeightTrendAnalyzer.java) — the analysis engine: sorting, moving average, regression, projection
- [DatedWeight.java](DatedWeight.java) — a single (date, weight) point in the series
- [TrendSummary.java](TrendSummary.java) — the computed result object returned to the UI
- [TrendChartView.java](TrendChartView.java) — custom Canvas view drawing the history and trend line
- [DashboardViewModel.java](DashboardViewModel.java) — invokes the analyzer and exposes the summary to the dashboard
