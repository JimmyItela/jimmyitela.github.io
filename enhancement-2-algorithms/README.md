# Enhancement Two — Algorithms & Data Structures

The **weight trend analysis engine** takes stored weight data and converts it into
the statistics and goal projection. The regression is done manually instead of
using a regression routine from some library.

## Functionality

- Reads weight history and creates a time series sorted by date.
- Calculates some summary statistics like current weight, total change, minimum,
  maximum and seven day moving average.
- Fitting a **linear least squares regression** line through the series by a
  single pass calculation of the regression sums to calculate the rate of change.
- Estimates the date to achieve the goal weight, but avoids divide by zero error
  for flat trend or series with less than two items.
- Draws the trend line on custom `TrendChartView`.

## Key files

- [WeightTrendAnalyzer.java](WeightTrendAnalyzer.java) — the analysis engine: sorting, moving average, regression, projection
- [DatedWeight.java](DatedWeight.java) — a single (date, weight) point in the series
- [TrendSummary.java](TrendSummary.java) — the computed result object returned to the UI
- [TrendChartView.java](TrendChartView.java) — custom Canvas view drawing the history and trend line
- [DashboardViewModel.java](DashboardViewModel.java) — invokes the analyzer and exposes the summary to the dashboard
