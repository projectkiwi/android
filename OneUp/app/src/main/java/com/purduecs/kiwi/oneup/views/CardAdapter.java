package com.purduecs.kiwi.oneup.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VISIBLE_THRESHOLD = 4;

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    List<Challenge> list = new ArrayList<>();

    private OnLoadMoreListener mLoadListener;
    LinearLayoutManager mLayout;

    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    private View.OnClickListener clickListener;

    public CardAdapter(RecyclerView recyclerView, List<Challenge> list, View.OnClickListener cardListener, final OnLoadMoreListener onLoadMoreListener) {
        this.list = list;
        this.mLoadListener = onLoadMoreListener;
        this.clickListener = cardListener;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            mLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    scrollCheck();
                }
            });
        }
    }

    private void scrollCheck() {
        if (mLayout != null) {
            totalItemCount = mLayout.getItemCount();
            visibleItemCount = mLayout.getChildCount();
            firstVisibleItem = mLayout.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                // End has been reached

                addItem(null);
                if (mLoadListener != null) {
                    mLoadListener.onLoadMore(new FinishedLoadingListener() {
                        @Override
                        public void finishedLoading() {
                            removeItem(null);
                        }
                    });
                }
                loading = true;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_BASIC) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newsfeed_item, parent, false);
            return new ViewHolder(itemView);
        } else if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        } else {
            throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_BASIC) {
            ViewHolder h = (ViewHolder) holder;
            h.cardid.setText(getItem(position).id);
            h.challenge=getItem(position);
            h.cardtitle.setText(list.get(position).name);
            h.cardimage.setImageResource(list.get(position).image);
            h.cardowner.setText("by " + list.get(position).owner);
            String categories = "";
            for (int i = 0; i < list.get(position).categories.length; i++) {
                categories += list.get(position).categories[i];
                categories += ", ";
            }
            categories = categories.substring(0, categories.length()-2);
            h.cardcategories.setText(categories);
            h.cardlikes.setText(Integer.toString(list.get(position).score));
            h.cardlikes.setTextOn(Integer.toString(list.get(position).score + 1));
            h.cardlikes.setTextOff(Integer.toString(list.get(position).score));
            h.cardtime.setText(Float.toString(list.get(position).time));
            h.carddesc.setText(list.get(position).desc);

            h.cardview.setOnClickListener(clickListener);
        } else {
            onBindFooterView(holder, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void addItem(Challenge item) {
        if (!list.contains(item)) {
            list.add(item);
            notifyItemInserted(list.size() - 1);
        }
    }

    private void removeItem(Challenge item) {
        int indexOfItem = list.indexOf(item);
        if (indexOfItem != -1) {
            this.list.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public void addItems(@NonNull List<Challenge> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void resetItems(@NonNull List<Challenge> list) {
        loading = false;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

        this.list.clear();
        addItems(list);
        scrollCheck();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Challenge getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View cardview;

        TextView cardid;
        ImageView cardimage;
        TextView cardtitle;
        TextView cardowner;
        TextView cardcategories;
        ToggleButton cardlikes;
        TextView cardtime;
        TextView carddesc;
        Challenge challenge;
        public ViewHolder(View itemView) {
            super(itemView);

            cardview = itemView;

            cardid = (TextView) itemView.findViewById(R.id.card_id);
            cardimage = (ImageView) itemView.findViewById(R.id.card_image);
            cardtitle = (TextView) itemView.findViewById(R.id.card_title);
            cardowner = (TextView) itemView.findViewById(R.id.card_winner);
            cardcategories = (TextView) itemView.findViewById(R.id.card_categories);
            cardlikes = (ToggleButton) itemView.findViewById(R.id.card_like_button);
            cardtime = (TextView) itemView.findViewById(R.id.card_time);
            carddesc = (TextView) itemView.findViewById(R.id.card_desc);
        }
    }

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        //noinspection ConstantConditions
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_loading_bar, parent, false);
        return new ProgressViewHolder(v);
    }

    public void onBindFooterView(RecyclerView.ViewHolder genericHolder, int position) {
        ((ProgressViewHolder) genericHolder).progressBar.setIndeterminate(true);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.loading_bar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(FinishedLoadingListener listener);
    }

    public interface FinishedLoadingListener {
        void finishedLoading();
    }
}