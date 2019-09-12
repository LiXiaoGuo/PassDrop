package com.linxiao.framework.widget.Section;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Extends on 2017/2/4.
 */
public interface SectionRecyclerHeadersAdapter<VH extends RecyclerView.ViewHolder> {
    /**
     * 得到头部id，如果返回的是-1，表示不需要绘制头部
     * @param position
     * @return
     */
    long getHeaderId(int position);

    /**
     * 创建头部布局的viewHolder
     * @param parent
     * @return
     */
    VH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * 绑定头部布局的viewHolder
     * @param holder
     * @param position
     */
    void onBindHeaderViewHolder(VH holder, int position);

    /**
     * 得到item总数
     * @return
     */
    int getItemCount();

    /**
     * 判断该数据是否为null，如果为null，则不绘制其分割线
     * @param position
     * @return
     */
    boolean isNull(int position);
}
