package com.linxiao.framework.widget.Section;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;

import androidx.recyclerview.widget.RecyclerView;

/**
 * GridLayout 用的粘性头部
 * 注：每一部分数据必须是列数的整倍数
 * Created by Extends on 2017/1/25.
 */
public class SectionDecoration extends RecyclerView.ItemDecoration{
    private static final String TAG="SectionDecoration";

    private HeaderViewCache headerProvider;//头部view缓存
    private final Rect mTempRect = new Rect();//临时Rect
    private SectionRecyclerHeadersAdapter adapter;//头部数据接口
    private final SparseArray<Rect> mHeaderRects = new SparseArray<>();//缓存的头部位置数据
    private boolean isStick = false;//头部view是否悬停
    private Paint mPaint;//分割线画笔
    private int mDividerHeight = 1;//分割线高度，默认为1px

    public SectionDecoration(int count, SectionRecyclerHeadersAdapter adapter,boolean isStick){
        this(count,adapter,isStick,0.5f);
    }

    public SectionDecoration(int count, SectionRecyclerHeadersAdapter adapter,boolean isStick,float dividerHeight){
        this.adapter = adapter;
        this.isStick = isStick;
        headerProvider = new HeaderViewCache(adapter);
        //Recycler列数
        this.count = count;
        mDividerHeight = SizeUtils.dp2px(dividerHeight);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffb1b1b1);
        mPaint.setStyle(Paint.Style.FILL);
        //当数据更新时清空缓存的头部view
        ((RecyclerView.Adapter) adapter).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headerProvider.invalidate();
            }
        });
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //得到真实的项数
        int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }
        //判断是否是各组的第一行，如果是，就设置头部偏移量
        if (isFirstLine(itemPosition)) {
            View header = getHeaderView(parent, itemPosition);
            setItemOffsetsForHeader(outRect, header);
        }
        //为分割线设置偏移量
        int i = itemPosition%count;
        if(i!=count-1){
            outRect.right=mDividerHeight;
            if(adapter.isNull(itemPosition)){
                outRect.right=0;
            }
        }
        outRect.top = outRect.top+mDividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
    {
        //画分割线
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent)
    {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            if (mPaint != null) {
                c.drawRect(child.getLeft(), child.getTop()-mDividerHeight, i%count==count-1?child.getRight():child.getRight()+mDividerHeight, child.getTop(), mPaint);
            }
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent)
    {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);

            if (mPaint != null&&i%count!=count-1) {
                c.drawRect(child.getRight(), child.getTop(),child.getRight()+mDividerHeight, child.getBottom()+mDividerHeight, mPaint);
            }
        }
    }

    /**
     * Sets the offsets for the first item in a section to make room for the header view
     *
     * @param itemOffsets rectangle to define offsets for the item
     * @param header      view used to calculate offset for the item
     */
    private void setItemOffsetsForHeader(Rect itemOffsets, View header) {
        itemOffsets.top = header.getHeight() + mTempRect.top + mTempRect.bottom;
    }

    private boolean isFirstLine(int pos){
        if(isFirstInGroup(pos)){
            return true;
        }else{
            if(pos-count<0){
                return true;
            }
            if (adapter.getHeaderId(pos - count)==adapter.getHeaderId(pos)) {
                return false;
            } else {
                return true;
            }
        }
    }

    private int count  = -1;

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //得到左右边距的位置信息
        int left = 0;
        int right = parent.getWidth();//如果要考虑padding，则应减去左右padding
        int headHeight = 0;
        final int childCount = parent.getChildCount();//缓存的数量，与数据项数不一样
        if (childCount <= 0 || adapter.getItemCount() <= 0) {
            return;
        }
        long preGroupId = -1;
        long groupId = -1;

        for (int i = 0; i < childCount; i++) {
            View itemView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(itemView);
            if (position == RecyclerView.NO_POSITION) {
                continue;
            }
            //只有各组第一个 并且 groupId!=-1 才绘制头部view
            preGroupId = groupId;
            groupId = adapter.getHeaderId(position);
            if(groupId==-1||groupId==preGroupId) {
                continue;
            }

            View header = headerProvider.getHeader(parent, position);
            //获取缓存的头部view的位置信息，如果没有则新创建
            Rect headerOffset = mHeaderRects.get(position);
            if (headerOffset == null) {
                headerOffset = new Rect();
                mHeaderRects.put(position, headerOffset);
            }
            //获取头部view的top、bottom位置信息
            float textY = itemView.getTop();
            headHeight = header.getHeight()+mDividerHeight;
            if(isStick){
                textY = Math.max(headHeight,itemView.getTop());
                int nextPosition = getNextGroupId(i,(int)groupId,childCount,parent);
                if(nextPosition!=-1){
                    View itemView1 = parent.getChildAt(nextPosition);
                    //判断下一个头部view是否到了与上一个头部view接触的临界值
                    //如果满足条件则把上一个头部view推上去
                    if(itemView1.getTop()<=headHeight+header.getHeight()){
                        textY=itemView1.getTop()-header.getHeight();
                    }
                }
            }
            //绘制头部view
            headerOffset.set(left, (int)textY - headHeight, right, (int)textY);
            drawHeader(c, header, headerOffset);
        }
    }

    /**
     * 获取下一个节点，如果没有则返回-1
     * @param count
     * @return
     */
    private int getNextGroupId(int id,int groupId,int count,RecyclerView parent){
        for (int i = id; i < count; i++) {
//            L.error("-------------------"+i+";;"+adapter.getHeaderId(parent.getChildAdapterPosition(parent.getChildAt(i)))+";;"+groupId);
            if(adapter.getHeaderId(parent.getChildAdapterPosition(parent.getChildAt(i)))!=groupId){
                return i;
            }
        }
        return -1;
    }

    private void drawHeader(Canvas canvas, View header, Rect offset) {
        canvas.save();
        //把(offset.left, offset.top)点设为原点
        canvas.translate(offset.left, offset.top);
        header.draw(canvas);
        canvas.restore();
    }


    /**
     * 判断是不是组中的第一个位置
     *
     * @param pos
     * @return
     */
    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            //判断前一个字符串 与 当前字符串 是否相同
            if (adapter.getHeaderId(pos-1)==adapter.getHeaderId(pos)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     *
     * @param parent
     * @param position
     * @return Header view
     */
    public View getHeaderView(RecyclerView parent, int position) {
        return headerProvider.getHeader(parent, position);
    }
}
