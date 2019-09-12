package com.linxiao.framework.widget.Section;

import androidx.collection.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


/**
 */
public class HeaderViewCache {

    private final SectionRecyclerHeadersAdapter mAdapter;
    private final LongSparseArray<View> mHeaderViews = new LongSparseArray<>();//缓存头部View

    public HeaderViewCache(SectionRecyclerHeadersAdapter adapter) {
        mAdapter = adapter;
    }

    public View getHeader(RecyclerView parent, int position) {
        long headerId = mAdapter.getHeaderId(position);

        View header = mHeaderViews.get(headerId);
        //判断缓存里是否有
        if (header == null) {
            //TODO - recycle views
            //获取头部View
            RecyclerView.ViewHolder viewHolder = mAdapter.onCreateHeaderViewHolder(parent);
            mAdapter.onBindHeaderViewHolder(viewHolder, position);
            header = viewHolder.itemView;
            //测量view的宽高，相当于onMeasure
            if (header.getLayoutParams() == null) {
                header.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            int widthSpec;
            int heightSpec;

            widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

            //获取view的宽高，可以考虑padding，这里宽度我实现的效果不能考虑padding，如果需要，可以参考高度的实现
            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    0, header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);
            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
            mHeaderViews.put(headerId, header);
        }
        return header;
    }

    /**
     * 清空缓存，当数据刷新时可以得到更新后的view
     */
    public void invalidate() {
        mHeaderViews.clear();
    }
}
