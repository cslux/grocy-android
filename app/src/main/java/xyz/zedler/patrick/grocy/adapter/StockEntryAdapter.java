package xyz.zedler.patrick.grocy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import xyz.zedler.patrick.grocy.R;
import xyz.zedler.patrick.grocy.model.StockEntries;
import xyz.zedler.patrick.grocy.model.StockEntry;
import xyz.zedler.patrick.grocy.util.DateUtil;
import xyz.zedler.patrick.grocy.util.NumUtil;

public class StockEntryAdapter
        extends RecyclerView.Adapter<StockEntryAdapter.ViewHolder> {

    private final static String TAG = StockEntryAdapter.class.getSimpleName();
    private final static boolean DEBUG = false;

    private Context context;
    private StockEntries stockEntries;
    private String selectedId;
    private StockEntryAdapterListener listener;

    public StockEntryAdapter(
            Context context,
            StockEntries stockEntries,
            String selectedId,
            StockEntryAdapterListener listener
    ) {
        this.context = context;
        this.stockEntries = stockEntries;
        this.selectedId = selectedId;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayoutContainer;
        private TextView textViewName, textViewSubtitle;
        private ImageView imageViewSelected;

        public ViewHolder(View view) {
            super(view);

            linearLayoutContainer = view.findViewById(
                    R.id.linear_stock_entry_container
            );
            textViewName = view.findViewById(R.id.text_stock_entry_name);
            textViewSubtitle = view.findViewById(R.id.text_stock_entry_subtitle);
            imageViewSelected = view.findViewById(R.id.image_stock_entry_selected);
        }
    }

    @NonNull
    @Override
    public StockEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StockEntryAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.row_stock_entry,
                        parent,
                        false
                )
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(
            @NonNull final StockEntryAdapter.ViewHolder holder,
            int position
    ) {
        StockEntry stockEntry = stockEntries.get(position);

        if(stockEntry.getStockId() == null) {
            // constructor of NO SPECIFIC/AUTO
            holder.textViewName.setText(context.getString(R.string.title_stock_entry_no_specific));
            holder.textViewSubtitle.setText(
                    context.getString(R.string.subtitle_stock_entry_no_specific)
            );
            if(selectedId == null) {
                holder.imageViewSelected.setVisibility(View.VISIBLE);
            }
        } else {
            DateUtil dateUtil = new DateUtil(context);

            // NAME
            holder.textViewName.setText(
                    context.getString(
                            R.string.subtitle_stock_entry_name,
                            dateUtil.getLocalizedDate(
                                    stockEntry.getPurchasedDate(),
                                    DateUtil.FORMAT_SHORT
                            )
                    )
            );

            // SUBTITLE
            String bbd = stockEntry.getBestBeforeDate().equals("2999-12-31")
                    ? context.getString(R.string.date_unlimited)
                    : dateUtil.getLocalizedDate(
                            stockEntry.getBestBeforeDate(),
                            DateUtil.FORMAT_SHORT
            );
            holder.textViewSubtitle.setText(
                    context.getString(
                            R.string.subtitle_stock_entry,
                            bbd,
                            NumUtil.trim(stockEntry.getAmount()),
                            context.getString(
                                    stockEntry.getOpen() == 0
                                            ? R.string.property_not_opened
                                            : R.string.property_opened
                            )
                    )
            );

            // SELECTED
            if(stockEntry.getStockId().equals(selectedId)) {
                holder.imageViewSelected.setVisibility(View.VISIBLE);
            }
        }

        // CONTAINER

        holder.linearLayoutContainer.setOnClickListener(
                view -> listener.onItemRowClicked(position)
        );
    }

    @Override
    public long getItemId(int position) {
        return stockEntries.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return stockEntries.size();
    }

    public interface StockEntryAdapterListener {
        void onItemRowClicked(int position);
    }
}