package com.android.vantage.Fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.android.vantage.R;
import com.android.vantageLogManager.Logger;

public class BarChartFragment extends BaseFragment {
	
	public interface BarChartItemClickListener{
		public void onBarItemClick(String xLabel);
	}

	@Override
	public void initViews() {
	}

	public static Fragment getInstance(Bundle b) {
		BarChartFragment f = new BarChartFragment();
		f.setArguments(b);
		return f;
	}

	public static GraphicalView getChartView(Context ctx,
			HashMap<String, Long> roomTimeStatistics, final BarChartItemClickListener listener) {
		int[] seriesColors = new int[] { Color.GRAY, Color.MAGENTA, Color.BLUE,
				R.color.chart_purple };
		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		final XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int c = 0;

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		final XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
		multiRenderer.setXLabels(0);
		multiRenderer.setChartTitle("Room Name vs Employee count");
		multiRenderer.setXTitle("");
		multiRenderer.setYTitle("No. Of Employees");

		/***
		 * Customizing graphs
		 */
		// setting text size of the title
		multiRenderer.setChartTitleTextSize(36);
		// setting text size of the axis title
		multiRenderer.setAxisTitleTextSize(32);
		multiRenderer.setLabelsColor(ctx.getResources().getColor(
				R.color.chart_blue));
		// setting text size of the graph lable
		multiRenderer.setLabelsTextSize(32);
		// setting zoom buttons visiblity
		multiRenderer.setZoomButtonsVisible(false);
		// setting pan enablity which uses graph to move on both axis
		multiRenderer.setPanEnabled(true, true);
		// setting click false on graph
		multiRenderer.setClickEnabled(false);
		// setting zoom to false on both axis
		multiRenderer.setZoomEnabled(true, true);
		// setting lines to display on y axis
		multiRenderer.setShowGridY(false);
		// setting lines to display on x axis
		multiRenderer.setShowGridX(false);
		// setting legend to fit the screen size
		multiRenderer.setFitLegend(true);
		// setting displaying line on grid
		multiRenderer.setShowGrid(false);
		// setting zoom to false
		multiRenderer.setZoomEnabled(true);
		// setting external zoom functions to false
		multiRenderer.setExternalZoomEnabled(false);
		// setting displaying lines on graph to be formatted(like using
		// graphics)
		multiRenderer.setAntialiasing(true);
		// setting to in scroll to false
		multiRenderer.setInScroll(false);
		// setting to set legend height of the graph
		multiRenderer.setLegendHeight(30);
		// setting x axis label align
		multiRenderer.setXLabelsAlign(Align.CENTER);
		// setting y axis label to align
		multiRenderer.setYLabelsAlign(Align.LEFT);
		// setting text style
		multiRenderer.setTextTypeface("sans_serif", Typeface.BOLD);
		
		// setting y axis max value, Since i'm using static values inside the
		// graph so i'm setting y max value to 4000.
		// if you use dynamic values then get the max y value and set here
		multiRenderer.setYAxisMin(0);
		// // setting used to move the graph on xaxiz to .5 to the right
		multiRenderer.setXAxisMin(-1);
		// // setting max values to be display in x axis

		// setting bar size or space between two bars
		multiRenderer.setBarSpacing(30);
		// Setting background color of the graph to transparent
		multiRenderer.setBackgroundColor(Color.WHITE);
		// Setting margin color of the graph to transparent
		multiRenderer.setMarginsColor(ctx.getResources().getColor(
				R.color.text_white));
		multiRenderer.setBarWidth(50f);
		
		multiRenderer.setAxesColor(ctx.getResources().getColor(
				R.color.text_color));
		multiRenderer.setXLabelsColor(ctx.getResources().getColor(
				R.color.text_color));

		multiRenderer.setYLabelsColor(0,
				ctx.getResources().getColor(R.color.text_color));
		multiRenderer.setApplyBackgroundColor(true);

		// setting the margin size for the graph in the order top, left, bottom,
		// right
		multiRenderer.setMargins(new int[] { 100, 100, 100, 100 });

		// for(int i=0; i< x.length;i++){
		// multiRenderer.addXTextLabel(i, mMonth[i]);
		// }

		// Adding incomeRenderer and expenseRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to
		// multipleRenderer
		// should be same
		// multiRenderer.addSeriesRenderer(incomeRenderer);
		// multiRenderer.addSeriesRenderer(expenseRenderer);

		XYSeries ser = new XYSeries("series");
		int xMax = roomTimeStatistics.size();
		long yMax = 0L;
		for (Entry<String, Long> entry : roomTimeStatistics.entrySet()) {
			// XYSeries itemSeries = new XYSeries(entry.getKey());
			if(entry.getKey() == null){
				continue;
			}
			if(entry.getKey().isEmpty()){
				continue;
			}
			ser.add(c, entry.getValue());
			multiRenderer.addXTextLabel(c, entry.getKey());
			// itemSeries.add(1, entry.getValue());
			// series.add(itemSeries);
			// dataset.addSeries(itemSeries);
			if (entry.getValue() > yMax) {
				yMax = entry.getValue();
			}

			c++;

		}
	
		if(yMax>5){
			// setting no of values to display in y axis
			multiRenderer.setYLabels(5);
		}else{
			multiRenderer.setYLabels((int) yMax);
		}
		multiRenderer.setXAxisMax(xMax);
		multiRenderer.setYAxisMax(yMax);
		dataset.addSeries(ser);
		XYSeriesRenderer itemRenderer = new XYSeriesRenderer();
		
		itemRenderer.setColor(ctx.getResources().getColor(R.color.xebia_color)); // color
																					// of
																					// the
		// graph set to cyan
		itemRenderer.setFillPoints(true);
		itemRenderer.setLineWidth(2);
		itemRenderer.setDisplayChartValues(true);
		itemRenderer.setDisplayChartValuesDistance(10);
		
		multiRenderer.addSeriesRenderer(itemRenderer);

		// this part is used to display graph on the xml

		// drawing bar chart
		final GraphicalView mChart = ChartFactory.getBarChartView(ctx, dataset,
				multiRenderer, Type.DEFAULT);
		multiRenderer.setClickEnabled(true);
		mChart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = mChart
						.getCurrentSeriesAndPoint();

				if (seriesSelection == null) {
				} else {
					for (int i = 0; i < dataset.getSeriesCount(); i++) {
//						multiRenderer.getSeriesRendererAt(i).setHighlighted(
//								i == seriesSelection.getPointIndex());

					}
//					String roomName = dataset.getSeriesAt(
//							seriesSelection.getPointIndex()).getTitle();
					mChart.repaint();
					Logger.error(
							"Series Clicked",
							""  + ": Point Index:"
									+ seriesSelection.getPointIndex()
									+ " :: Series Index"
									+ seriesSelection.getSeriesIndex());
					String xLabel = multiRenderer.getXTextLabel((double) seriesSelection.getPointIndex());
					Logger.info("Series Clicked", xLabel+"");
					if(listener != null){
						listener.onBarItemClick(xLabel);
					}
				}
			}
		});
		// adding the view to the linearlayout
		return mChart;

	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return R.layout.linear_container_chart;
	}

}
