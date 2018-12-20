package com.test.voicecontroller;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HeaderAndFooterRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER_VIEW = Integer.MIN_VALUE / 2;

    /**
     * RecyclerVeiw使用的，真正的Adapter(业务侧自己使用的)
     */
    private RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeaderViewsCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };

    public HeaderAndFooterRecyclerViewAdapter() {
    }

    public HeaderAndFooterRecyclerViewAdapter(RecyclerView.Adapter innerAdapter) {
        setAdapter(innerAdapter);
    }

    public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {

        if (adapter == null) {
            throw new RuntimeException("your adapter must be not null");
        }

        if (mInnerAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), mInnerAdapter.getItemCount());
            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
        }

        this.mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
        notifyItemRangeInserted(getHeaderViewsCount(), mInnerAdapter.getItemCount());
    }

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    public void addHeaderView(int index, View header) {
        if (index >= 0 && index <= getHeaderViewsCount()) {
            mHeaderViews.add(index, header);
            this.notifyDataSetChanged();
        }
    }

    public void addHeaderView(View header) {
        mHeaderViews.add(header);

        this.notifyDataSetChanged();
    }


    public void addFooterView(View footer) {
        mFooterViews.add(footer);

        this.notifyItemInserted(getItemCount());
    }

    /**
     * 返回第一个FoView
     */
    public View getFooterView() {
        return getFooterViewsCount() > 0 ? mFooterViews.get(0) : null;
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    /**
     * 是否包含指定的HeaderView
     */
    public boolean containHeaderView(View headerView) {
        for (View view : mHeaderViews) {
            if (view != null && view.equals(headerView)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含指定的FooterView
     */
    public boolean containFooterView(View footerView) {
        for (View view : mFooterViews) {
            if (view != null && view.equals(footerView)) {
                return true;
            }
        }

        return false;
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    public boolean isHeader(int position) {
        return getHeaderViewsCount() > 0 && position == 0;
    }

    public boolean isFooter(int position) {
        int lastPosition = getItemCount() - 1;
        return getFooterViewsCount() > 0 && position == lastPosition;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewCount = getHeaderViewsCount();
        if (viewType < TYPE_HEADER_VIEW + headerViewCount) {
            return new ViewHolder(mHeaderViews.get(viewType - TYPE_HEADER_VIEW));
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < 0) {
            return new ViewHolder(mFooterViews.get(viewType - TYPE_FOOTER_VIEW));
        } else {
            if (mInnerAdapter == null) return null;
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerViewsCount = getHeaderViewsCount();
        if (mInnerAdapter != null) {
            if (position >= headerViewsCount && position < headerViewsCount + mInnerAdapter.getItemCount()) {
                mInnerAdapter.onBindViewHolder(holder, position - headerViewsCount);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        int headerViewsCount = getHeaderViewsCount();
        if (mInnerAdapter != null) {
            if (position >= headerViewsCount && position < headerViewsCount + mInnerAdapter.getItemCount()) {
                mInnerAdapter.onBindViewHolder(holder, position - headerViewsCount, payloads);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (mHeaderViews != null && mHeaderViews.size() > 0) {
            for (View view : mHeaderViews) {
                if (holder.itemView.equals(view)) {
                    return;
                }
            }
        }

        if (mFooterViews != null && mFooterViews.size() > 0) {
            for (View view : mFooterViews) {
                if (holder.itemView.equals(view)) {
                    return;
                }
            }
        }
        mInnerAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof ViewHolder)) {
            if (mInnerAdapter != null) {
                mInnerAdapter.onViewRecycled(holder);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (!(holder instanceof ViewHolder)) {
            if (mInnerAdapter != null) {
                mInnerAdapter.onViewDetachedFromWindow(holder);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mInnerAdapter == null)
            return getHeaderViewsCount() + getFooterViewsCount();
        return getHeaderViewsCount() + getFooterViewsCount() + mInnerAdapter.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        int innerCount = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();
        int headerViewsCount = getHeaderViewsCount();
        if (position < getHeaderViewsCount()) {
            return TYPE_HEADER_VIEW + position;
        } else if (headerViewsCount <= position && position < headerViewsCount + innerCount) {
            return position;
        } else {
            return TYPE_FOOTER_VIEW + position - headerViewsCount - innerCount;
        }

    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = mInnerAdapter == null ? 0 : mInnerAdapter.getItemCount();
        int headerViewsCount = getHeaderViewsCount();
        if (position < headerViewsCount) {
            return TYPE_HEADER_VIEW + position;
        } else if (mInnerAdapter != null && headerViewsCount <= position && position < headerViewsCount + innerCount) {
            int innerItemViewType = mInnerAdapter.getItemViewType(position - headerViewsCount);
            if (innerItemViewType >= Integer.MAX_VALUE / 2) {
                throw new IllegalArgumentException("your adapter's return value of getViewTypeCount() must < Integer.MAX_VALUE / 2");
            }
            return innerItemViewType;
        } else {
            return TYPE_FOOTER_VIEW + position - headerViewsCount - innerCount;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
