package com.phaseii.rxm.roomies.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.phaseii.rxm.roomies.R;
import com.phaseii.rxm.roomies.model.MiscExpense;
import com.phaseii.rxm.roomies.model.SortType;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.phaseii.rxm.roomies.helper.RoomiesConstants.BILLS;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.GROCERY;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.OTHERS;
import static com.phaseii.rxm.roomies.helper.RoomiesConstants.VEGETABLES;

/**
 * Created by Snehankur on 5/6/2015.
 */
public class DetailExpenseDataAdapter extends RecyclerView.Adapter<DetailExpenseDataAdapter
		.ViewHolder> {

	private static final int HEADER = 3;
	private Context mContext;
	private List<MiscExpense> miscExpenses;
	private int LIST = 0;
	private int ALT_LIST = 1;
	private int start;
	private int day;
	private boolean isSorted;
	private SortType sortType;
	private Calendar cal = Calendar.getInstance();
	private Typeface typeface;

	public DetailExpenseDataAdapter(List<MiscExpense> miscExpenses, SortType sortType,
	                                Context mContext) {
		this.miscExpenses = miscExpenses;
		this.mContext = mContext;
		this.sortType = sortType;
		this.typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/VarelaRound-Regular" +
				".ttf");
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == HEADER) {
			view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_header, parent,
					false);
		} else if (viewType == LIST) {
			view = LayoutInflater.from(mContext).inflate(R.layout.detail_expense_list, parent,
					false);
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.detail_expense_list_alt, parent,
					false);
		}
		ViewHolder vhItem = new ViewHolder(view, viewType);
		return vhItem;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (holder.holderId == HEADER) {
			createCombinedChart(holder.lineChart);
			holder.month.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
					"fonts/VarelaRound-Regular.ttf"));
			holder.month.setText(new DateFormatSymbols().getMonths()[Calendar
					.getInstance().get(Calendar.MONTH)]);
		} else {
			holder.amount.setText(String.valueOf(miscExpenses.get(position - 1).getAmount()));
			holder.description.setText(miscExpenses.get(position - 1).getDescription());
			holder.quantity.setText(String.valueOf(miscExpenses.get(position - 1).getQuantity()));
			holder.date.setText(new SimpleDateFormat("dd").format(miscExpenses.get(position - 1)
					.getTransactionDate()));
			holder.date.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
					"fonts/VarelaRound-Regular.ttf"));
			ShapeDrawable shapeDrawable = new ShapeDrawable();
			Paint paint = shapeDrawable.getPaint();
			ShapeDrawable time = new ShapeDrawable(new OvalShape());
			switch (miscExpenses.get(position - 1).getType()) {
				case BILLS:
					holder.expenseImage.setImageResource(R.drawable.ic_bills_selected);
					time.getPaint().setColor(mContext.getResources().getColor(R.color.blue));
					holder.timelineDot.setBackgroundDrawable(time);
					if (holder.holderId == LIST) {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(
										R.drawable.timeline_bubble_blue));
					} else {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(
										R.drawable.timeline_bubble_blue_inverted));
					}
					paint.setColor(mContext.getResources().getColor(R.color.blue));
					paint.setStyle(Paint.Style.STROKE);
					paint.setStrokeWidth(3);
					holder.timelineCard.setBackgroundDrawable(shapeDrawable);

					break;
				case GROCERY:
					holder.expenseImage.setImageResource(R.drawable.ic_grocery_selected);
					time.getPaint().setColor(mContext.getResources().getColor(R.color.orange));
					holder.timelineDot.setBackgroundDrawable(time);
					if (holder.holderId == LIST) {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(
										R.drawable.timeline_bubble_orange));
					} else {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(R.drawable
										.timeline_bubble_orange_inverted));
					}
					paint.setColor(mContext.getResources().getColor(R.color.orange));
					paint.setStyle(Paint.Style.STROKE);
					paint.setStrokeWidth(3);
					holder.timelineCard.setBackgroundDrawable(shapeDrawable);
					break;
				case VEGETABLES:
					holder.expenseImage.setImageResource(R.drawable.ic_vegetable_selected);
					time.getPaint().setColor(mContext.getResources().getColor(R.color.lime));
					holder.timelineDot.setBackgroundDrawable(time);
					if (holder.holderId == LIST) {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(
										R.drawable.timeline_bubble_green));
					} else {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(R.drawable
										.timeline_bubble_green_inverted));
					}
					paint.setColor(mContext.getResources().getColor(R.color.lime));
					paint.setStyle(Paint.Style.STROKE);
					paint.setStrokeWidth(3);
					holder.timelineCard.setBackgroundDrawable(shapeDrawable);
					break;
				case OTHERS:
					holder.expenseImage.setImageResource(R.drawable.ic_others_selected);
					time.getPaint().setColor(mContext.getResources().getColor(R.color.yellow));
					holder.timelineDot.setBackgroundDrawable(time);
					if (holder.holderId == LIST) {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(
										R.drawable.timeline_bubble_yellow));
					} else {
						holder.timelineBanner.setBackgroundDrawable(
								mContext.getResources().getDrawable(R.drawable
										.timeline_bubble_yellow_inverted));
					}
					paint.setColor(mContext.getResources().getColor(R.color.yellow));
					paint.setStyle(Paint.Style.STROKE);
					paint.setStrokeWidth(3);
					holder.timelineCard.setBackgroundDrawable(shapeDrawable);
					break;
			}
		}
	}

	@Override
	public int getItemCount() {
		return miscExpenses.size() + 1;
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position))
			return HEADER;

		return position % 2;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private int holderId;
		private TextView date;
		private TextView description;
		private TextView quantity;
		private TextView amount;
		private View itemView;
		private ImageView expenseImage;
		private RelativeLayout timelineCard;
		private View timelineDot;
		private RelativeLayout timelineBanner;
		private LineChart lineChart;
		private TextView month;

		public ViewHolder(View itemView, int viewType) {
			super(itemView);
			this.itemView = itemView;
			this.holderId = viewType;
			if (viewType == HEADER) {
				lineChart = (LineChart) itemView.findViewById(R.id.trend_chart);
				month = (TextView) itemView.findViewById(R.id.month);
			} else {
				date = (TextView) itemView.findViewById(R.id.date);
				quantity = (TextView) itemView.findViewById(R.id.quantity);
				description = (TextView) itemView.findViewById(R.id.description);
				amount = (TextView) itemView.findViewById(R.id.amount);
				expenseImage = (ImageView) itemView.findViewById(R.id.expense_icon);
				timelineCard = (RelativeLayout) itemView.findViewById(R.id.timeline_card);
				timelineDot = itemView.findViewById(R.id.timeline_dot);
				timelineBanner = (RelativeLayout) itemView.findViewById(R.id.timeline_banner);
			}
		}
	}

	public void createCombinedChart(LineChart lineChart) {
		lineChart.setDescription("");
		lineChart.setDrawGridBackground(false);
		lineChart.setHighlightEnabled(false);
		lineChart.setTouchEnabled(false);
		lineChart.setDragEnabled(true);
		lineChart.setScaleEnabled(false);


		YAxis rightAxis = lineChart.getAxisRight();
		rightAxis.setDrawGridLines(false);
		rightAxis.setEnabled(false);

		YAxis leftAxis = lineChart.getAxisLeft();
		leftAxis.setDrawGridLines(false);
		leftAxis.setEnabled(false);

		XAxis xAxis = lineChart.getXAxis();
		xAxis.setEnabled(true);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setTextColor(Color.WHITE);
		xAxis.setTypeface(typeface);

		lineChart.setData(getLineChart(miscExpenses));
		lineChart.setPinchZoom(false);
		lineChart.setDoubleTapToZoomEnabled(false);
		lineChart.setDrawGridBackground(false);
		lineChart.animateY(500);
		lineChart.animateX(500);
		lineChart.setPinchZoom(false);
		lineChart.getLegend().setEnabled(false);
		lineChart.setNoDataText("No Miscellaneous expenses yet");
		lineChart.setBackgroundColor(mContext.getResources().getColor(R.color.primary_dark));
	}

	private LineData getLineChart(List<MiscExpense> miscExpenses) {

		List<MiscExpense> miscExpenseList = new ArrayList<>();
		miscExpenseList.addAll(miscExpenses);
		int index = 0;
		ArrayList<Entry> entries = new ArrayList<Entry>();
		ArrayList<String> labels = new ArrayList<>();
		if (miscExpenseList.size() > 0) {
			switch (sortType) {
				case DATE_ASC:
					cal.setTime(miscExpenseList.get(0).getTransactionDate());
					start = cal.get(Calendar.DAY_OF_MONTH);
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), (day - start)));
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == (day - start)) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case DATE_DESC:
					cal.setTime(miscExpenseList.get(0).getTransactionDate());
					start = cal.get(Calendar.DAY_OF_MONTH);
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), (start - day)));
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == (start - day)) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case AMOUNT:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;

				case QUANTITY:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getQuantity(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getQuantity();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case BILLS:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case GROCERY:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case VEGETABLES:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				case OTHERS:
					index = 0;
					for (MiscExpense misc : miscExpenseList) {
						cal.setTime(misc.getTransactionDate());
						day = cal.get(Calendar.DAY_OF_MONTH);
						if (!labels.contains(String.valueOf(day))) {
							labels.add(String.valueOf(day));
							entries.add(new Entry(misc.getAmount(), index));
							index++;
						} else {
							for (Entry entry : entries) {
								if (entry.getXIndex() == labels.indexOf(String.valueOf(day))) {
									float val = entry.getVal();
									val = val + misc.getAmount();
									entry.setVal(val);
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}

		LineDataSet set = new LineDataSet(entries, "Daily Expense Report");
		set.setLineWidth(3f);
		set.setCircleColor(mContext.getResources().getColor(R.color.primary_dark));
		set.setCircleSize(5f);
		set.setFillColor(mContext.getResources().getColor(R.color.primary));
		set.setDrawValues(true);
		set.setValueTextSize(10f);
		set.setValueTextColor(Color.WHITE);
		set.setAxisDependency(YAxis.AxisDependency.LEFT);
		set.setDrawFilled(true);
		set.setColor(Color.WHITE);
		set.setDrawCubic(false);
		set.disableDashedLine();
		set.setValueTypeface(typeface);
		set.setFillColor(mContext.getResources().getColor(R.color.primary));
		LineData lineData = new LineData(labels, set);
		return lineData;
	}

	public void addItemsToList(List<MiscExpense> miscExpenses, SortType sortType) {
		this.miscExpenses = miscExpenses;
		this.sortType = sortType;
	}
}