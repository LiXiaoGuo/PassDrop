package com.linxiao.framework.adapter;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * RecyclerView 的 GridLayoutManager 形式下的分割线
 *
 * 如果GridLayoutMananger的GridLayoutManager.SpanSizeLookup是自定义的，
 * 建议设置({@link GridLayoutManager.SpanSizeLookup#setSpanIndexCacheEnabled(boolean)} )为true
 *
 * @author Extends
 * @date 2016/8/10
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    /**
     * 分割线高度，默认为1px
     */
    private int mDividerHeight = 1;

    private Boolean isFirstAndLastRawEnable = false;

    /**
     * 是否绘制Grid的边界
     */
    private Boolean isDrawGridBorder = false;

    /**
     * 仅绘制指定的GridSpanSize的边界
     */
    private int drawGridSpanSizeBorder = -1;


    /**
     * 默认颜色为透明
     *
     * @param dividerHeight 分割线高度，单位sp
     */
    public DividerItemDecoration(float dividerHeight) {
        this(dividerHeight, TypedValue.COMPLEX_UNIT_DIP, Color.TRANSPARENT);
    }

    /**
     * @param dividerHeight 分割线高度，单位sp
     * @param dividerColor  分割线颜色
     */
    public DividerItemDecoration(float dividerHeight, int dividerColor) {
        this(dividerHeight, TypedValue.COMPLEX_UNIT_DIP, dividerColor);
    }

    /**
     * @param dividerHeight 分割线高度
     * @param typedValue    分割线高度的单位
     * @param dividerColor  分割线颜色
     */
    public DividerItemDecoration(float dividerHeight, int typedValue, int dividerColor) {
        mDividerHeight = (int) applyDimension(typedValue, dividerHeight);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int color = mPaint.getColor();
        //如果填充颜色是透明的，则可以不用填充
        if (color >> 24 == 0) {
            return;
        }

        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        //先判断是那种类型
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            //TODO 暂未支持瀑布流布局的填充间隔
        } else if (layoutManager instanceof GridLayoutManager) {
            drawGrid(c, parent, spanCount, childCount, (GridLayoutManager) layoutManager);
        } else if (layoutManager instanceof LinearLayoutManager) {
            drawLinearHorizontal(c, parent, spanCount, childCount, (LinearLayoutManager) layoutManager);
            drawLinearVertical(c, parent, spanCount, childCount, (LinearLayoutManager) layoutManager);
        }

    }

    /**
     * 获取一行有几个
     *
     * @param parent
     * @return
     */
    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof LinearLayoutManager) {
            spanCount = 1;
        }
        return spanCount;
    }

    /**
     * 判断是否是最后一列
     *
     * @param parent
     * @param pos
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        //childcount从1开始算，所以应该先减1
        childCount--;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        //如果是GridLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            int or = ((GridLayoutManager) layoutManager).getOrientation();
            //判断是否是竖着排列
            if (or == LinearLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount) {
                    return true;
                }
            }
        }
        //如果是瀑布流
        else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount) {
                    return true;
                }
            }
        } else {//如果是LinearLayoutManager
            int orientation = ((LinearLayoutManager) layoutManager).getOrientation();
            //竖着的LinearLayoutManager只有一列，所以既是第一列，也是最后一列
            //只有当前项等于总数时表示是最后一列了
            return orientation == LinearLayoutManager.VERTICAL || pos == childCount;
        }
        return false;
    }

    /**
     * 判断是否是最后一行
     *
     * @return
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        childCount--;//childcount从1开始算，所以应该先减1
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int or = ((GridLayoutManager) layoutManager).getOrientation();
            if (or == LinearLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) {
                    return true;
                }
            } else {
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) {
                    return true;
                }
            } else {// StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        } else if (layoutManager instanceof LinearLayoutManager) {
            int or = ((LinearLayoutManager) layoutManager).getOrientation();
            //竖着排列时，如果当前项数等于childCount时就表示到最后一行了
            //横着排列时，每一项都是最后一行
            return or != LinearLayoutManager.VERTICAL || pos == childCount;
        }
        return false;
    }

    /**
     * 判断是第一列
     */
    private boolean isFirstColum(RecyclerView parent, int pos, int spanCount, int childCount) {
        //childcount从1开始算，所以应该先减1
        childCount--;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        //如果是GridLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            int or = ((GridLayoutManager) layoutManager).getOrientation();
            //判断是否是竖着排列
            if (or == LinearLayoutManager.VERTICAL) {
                if (pos % spanCount == 0) {
                    return true;
                }
            } else {
                if (pos < spanCount) {
                    return true;
                }
            }
        }else if(layoutManager instanceof LinearLayoutManager){
            int or = ((LinearLayoutManager) layoutManager).getOrientation();
            //判断是否是竖着排列
            if (or == LinearLayoutManager.VERTICAL) {
                return true;
            } else if (pos == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isFirstRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        //childcount从1开始算，所以应该先减1
        childCount--;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        //如果是GridLayoutManager
        if (layoutManager instanceof GridLayoutManager) {
            int or = ((GridLayoutManager) layoutManager).getOrientation();
            //判断是否是竖着排列
            if (or == LinearLayoutManager.VERTICAL) {
                if (pos < spanCount) {
                    return true;
                }
            } else {
                if (pos % spanCount == 0) {
                    return true;
                }
            }
        }else if (layoutManager instanceof LinearLayoutManager) {
            int or = ((LinearLayoutManager) layoutManager).getOrientation();
            return or == LinearLayoutManager.VERTICAL && pos == 0;
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();

        if (itemPosition >= 0) {
            setRect(outRect, itemPosition, parent, spanCount, childCount);
        }

    }

    /**
     * 设置Rect的矩形范围
     *
     * @param outRect      输出的矩形
     * @param itemPosition 第几项数
     * @param parent       RecyclerView
     * @param spanCount    一行有几个
     * @param childCount   该适配器中的项目总数
     */
    private void setRect(Rect outRect, int itemPosition, RecyclerView parent, int spanCount, int childCount) {
        //先判断是那种类型
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            setStaggeredGridRect(outRect, itemPosition, parent, spanCount, childCount, (StaggeredGridLayoutManager) layoutManager);
        } else if (layoutManager instanceof GridLayoutManager) {
            setGridRect(outRect, itemPosition, parent, spanCount, childCount, (GridLayoutManager) layoutManager);
        } else if (layoutManager instanceof LinearLayoutManager) {
            setLinearRect(outRect, itemPosition, parent, spanCount, childCount, (LinearLayoutManager) layoutManager);
        }
    }

    /**
     * 单位转换 比如sp转px
     *
     * @param unit 单位
     * @param size 值
     * @return 返回转换后的结果
     */
    private float applyDimension(int unit, float size) {
        return TypedValue.applyDimension(unit, size, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 绘制StaggeredGridLayoutManager瀑布流的间隔
     * FIXME 暂时未实现绘制StaggeredGridLayoutManager瀑布流的间隔
     */
    private void setStaggeredGridRect(Rect outRect, int itemPosition, RecyclerView parent, int spanCount, int childCount, StaggeredGridLayoutManager manager) {
        throw new IllegalStateException("暂时未实现绘制StaggeredGridLayoutManager瀑布流的间隔");
    }

    /**
     * 绘制LinearLayoutManager的间隔
     */
    private void setLinearRect(Rect outRect, int itemPosition, RecyclerView parent, int spanCount, int childCount, LinearLayoutManager manager) {
        int or = manager.getOrientation();
        if (or == LinearLayoutManager.VERTICAL) {
            // 如果是最后一行，则不需要绘制底部
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                if (isFirstAndLastRawEnable) {//当尾部需要间隔时
                    outRect.set(0, 0, 0, mDividerHeight);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }else {// 竖向的LinearLayoutManager每一项都是最后一列
                if(isFirstRaw(parent, itemPosition, spanCount, childCount)&&isFirstAndLastRawEnable){//当顶部需要间隔时
                    outRect.set(0, mDividerHeight, 0, mDividerHeight);
                }else {
                    outRect.set(0, 0, 0, mDividerHeight);
                }
            }
        } else {
            //如果是最后一列，则不需要绘制右边

            if (isLastColum(parent, itemPosition, spanCount, childCount)) {
                if(isFirstAndLastRawEnable){
                    outRect.set(0, 0, mDividerHeight, 0);
                }else{
                    outRect.set(0, 0, 0, 0);
                }
            } else if(isFirstColum(parent, itemPosition, spanCount, childCount)){
                if(isFirstAndLastRawEnable){
                    outRect.set(mDividerHeight, 0, mDividerHeight, 0);
                }else{
                    outRect.set(0, 0, mDividerHeight, 0);
                }
            } else{
                    outRect.set(0, 0, mDividerHeight, 0);
            }
        }
    }

    /**
     * 设置是否给头部和尾部设置分割线
     *
     * @return
     */
    public void setFirstAndLastRawEnable(Boolean isFirstAndLastRawEnable) {
        this.isFirstAndLastRawEnable = isFirstAndLastRawEnable;
    }

    /**
     * 表示当前行还有几个item，如果是0，表示还有一个，如果是-1，表示这个item就是当前行的最后一个了
     */
    int hangCout = -1;
    /**
     * 表示当前行应该有几个item，hangSpanCount<=spanCount
     */
    int hangSpanCount = 1;
    /**
     * grid是否是最后一行或一列
     */
    boolean gridIsLast = false;

    /**
     * 绘制GridLayoutManager的间隔
     */
    private void setGridRect(Rect outRect, int itemPosition, RecyclerView parent, int spanCount, int childCount, GridLayoutManager manager) {
        int or = manager.getOrientation();
        GridLayoutManager.SpanSizeLookup ssl = manager.getSpanSizeLookup();
        //表示没有记录
        if(hangCout == -1){
            int tempItem = itemPosition-1;
            int tempCount = 0;
            int sp = manager.getItemCount()-1;
//            System.out.println(itemPosition+";;;"+sp);
            gridIsLast = false;
            //判断当前行有几个item
            while(tempCount<spanCount){
                tempItem++;
                tempCount += ssl.getSpanSize(tempItem);
//                if(itemPosition>=40){
//                    System.out.println("---tempItem = " + tempItem);
//                }
                if(tempItem == sp){
                    gridIsLast = true;
                    break;
                }
            }

            //表示这一行的item加起来没有spanCount多，但是加上下一行的第一个就大于spanCount了
            if(tempCount > spanCount){
                tempItem--;
                //计算当前行的item总数，用spanCount除以当前行最后一个item的SpanSize来表示
                tempCount = spanCount/ssl.getSpanSize(tempItem);
            }else if(tempCount<spanCount){
                tempCount = spanCount/ssl.getSpanSize(tempItem);
            }
            //tempItem就是这一行最后一个的值
            //这一行有几个
            hangCout = tempItem - itemPosition;
            hangSpanCount = tempCount;
        }
        int sc = hangSpanCount;
        hangCout--;
//        int i1 = ssl.getSpanSize(itemPosition);
        //item是当前行的第几个
        int spanIndex = ssl.getSpanIndex(itemPosition,spanCount);
        //item是第几行
        int i3 = ssl.getSpanGroupIndex(itemPosition,spanCount);
        //计算偏移量
        float iv = mDividerHeight*1.0f / sc;
//        if(itemPosition>40){
//            System.out.println("==================itemPosition = " + itemPosition + ";gridIsLast = " + gridIsLast);
//        }
        if (or == LinearLayoutManager.VERTICAL) {
            if(isDrawGridBorder){
                int top = 0;
                int bottom = mDividerHeight;
                //判断是否是第一行
                if(i3 == 0){
                    top = mDividerHeight;
                }
                int left = ((int) ((sc - spanIndex) * iv));
                int right = ((int) ((1 + spanIndex) * iv));
                //防止去掉小数后为0的情况
                if(left == 0) left = 1;
                if(right == 0) right = 1;
                outRect.set(left, top, right, bottom);
            }else{
                if (gridIsLast) {
                    outRect.set(((int) ((spanIndex % sc) * iv)), 0, ((int) ((sc - 1 - spanIndex % sc) * iv)), 0);
                } else {
                    outRect.set(((int) ((spanIndex % sc) * iv)), 0, ((int) ((sc - 1 - spanIndex % sc) * iv)), mDividerHeight);
                }
            }

        } else {
            if (isLastColum(parent, itemPosition, sc, childCount)) {
                outRect.set(0, ((int) ((spanIndex % sc) * iv)), 0, ((int) ((sc - 1 - spanIndex % sc) * iv)));
            } else {
                outRect.set(0, ((int) ((spanIndex % sc) * iv)), mDividerHeight, ((int) ((sc - 1 - spanIndex % sc) * iv)));
            }
        }
    }


    /**
     * 填充横向的间隔的颜色
     */
    private void drawLinearHorizontal(Canvas c, RecyclerView parent, int spanCount, int childCount, LinearLayoutManager manager) {
        int or = manager.getOrientation();
        //如果是横向的LinearLayoutManager则不需要填充横向的间隔
        if (or == LinearLayoutManager.HORIZONTAL) {
            return;
        }

        for (int i = 0; i < childCount; i++) {
            final View child = manager.findViewByPosition(i);
            if (child == null) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDividerHeight;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;

            // 如果是最后一行，则不需要绘制底部
            // 竖向的LinearLayoutManager每一项都是最后一列
            if (!isLastRaw(parent, i, spanCount, childCount)) {
                if (mPaint != null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 填充竖向的间隔的颜色
     */
    private void drawLinearVertical(Canvas c, RecyclerView parent, int spanCount, int childCount, LinearLayoutManager manager) {
        int or = manager.getOrientation();
        //如果是竖向的LinearLayoutManager则不需要填充竖向的间隔
        if (or == LinearLayoutManager.VERTICAL) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            final View child = manager.findViewByPosition(i);
            if (child == null) {
                continue;
            }
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;

            //如果是最后一列，则不需要绘制右边
            //横向的LinearLayoutManager每一项都是最后一行
            if (!isLastColum(parent, i, spanCount, childCount)) {
                if (mPaint != null) {
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }

    /**
     * 填充Grid的间隔的颜色
     */
    private void drawGrid(Canvas c, RecyclerView parent, int spanCount, int childCount, GridLayoutManager manager) {
        if (mPaint == null) {
            return;
        }
        int shield = -1;
        GridLayoutManager.SpanSizeLookup ssl = manager.getSpanSizeLookup();
        for (int i = 0; i < childCount; i++) {
            final View child = manager.findViewByPosition(i);
            if (child == null) {
                continue;
            }
            //item所占用的spanSize
            int i1 = ssl.getSpanSize(i);
            //item是当前行的第几个
            int spanIndex = ssl.getSpanIndex(i,spanCount);
            //item是第几行的
            int i3 = ssl.getSpanGroupIndex(i,spanCount);

            if(isDrawGridBorder){
                //如果需要绘制边界

                //判断当前item是否需要过滤
                if(drawGridSpanSizeBorder>0 && drawGridSpanSizeBorder != i1) {
                    shield = i3;
                    continue;
                }


                //实际上，Grid的布局中，可能有些item的size不是1，所以如果需要边界绘制得合理，
                // 必须对size!=0的item进行过度绘制
                if(i1 != 1){
                    c.drawRect(child.getLeft()-mDividerHeight,child.getTop()-mDividerHeight,child.getLeft(),child.getBottom()+mDividerHeight,mPaint);
                    c.drawRect(child.getRight(),child.getTop()-mDividerHeight,child.getRight()+mDividerHeight,child.getBottom()+mDividerHeight,mPaint);
                    c.drawRect(child.getLeft(),child.getTop()-mDividerHeight,child.getRight(),child.getTop(),mPaint);
                    c.drawRect(child.getLeft(),child.getBottom(),child.getRight(),child.getBottom()+mDividerHeight,mPaint);
                }else{
                    //判断是否是第一行
                    if(i3 == 0){
                        //是第一行则绘制TOP和Bottom
                        //判断是否是第一列，如果是还需要绘制Left和Right
                        if(spanIndex == 0){
                            c.drawRect(child.getLeft()-mDividerHeight,child.getTop()-mDividerHeight,child.getLeft(),child.getBottom()+mDividerHeight,mPaint);
                            c.drawRect(child.getRight(),child.getTop()-mDividerHeight,child.getRight()+mDividerHeight,child.getBottom()+mDividerHeight,mPaint);
                        }else{
                            c.drawRect(child.getRight(),child.getTop()-mDividerHeight,child.getRight()+mDividerHeight,child.getBottom()+mDividerHeight,mPaint);
                        }
                        c.drawRect(child.getLeft(),child.getTop()-mDividerHeight,child.getRight(),child.getTop(),mPaint);
                        c.drawRect(child.getLeft(),child.getBottom(),child.getRight(),child.getBottom()+mDividerHeight,mPaint);
                    }else if(spanIndex == 0){
                        c.drawRect(child.getLeft()-mDividerHeight,child.getTop(),child.getLeft(),child.getBottom()+mDividerHeight,mPaint);
                        c.drawRect(child.getRight(),child.getTop(),child.getRight()+mDividerHeight,child.getBottom()+mDividerHeight,mPaint);
                        c.drawRect(child.getLeft(),child.getBottom(),child.getRight(),child.getBottom()+mDividerHeight,mPaint);
                        //判断上一行是否过滤掉了，如果过滤了就表示上一行的Bottom没有绘制，需要在当前行绘制Top来替代上一行的Bottom
                        if(shield == i3-1){
                            c.drawRect(child.getLeft()-mDividerHeight,child.getTop()-mDividerHeight,child.getRight()+mDividerHeight,child.getTop(),mPaint);
                        }
                    }else{
                        c.drawRect(child.getRight(),child.getTop(),child.getRight()+mDividerHeight,child.getBottom()+mDividerHeight,mPaint);
                        c.drawRect(child.getLeft(),child.getBottom(),child.getRight(),child.getBottom()+mDividerHeight,mPaint);
                        //判断上一行是否过滤掉了，如果过滤了就表示上一行的Bottom没有绘制，需要在当前行绘制Top来替代上一行的Bottom
                        if(shield == i3-1){
                            c.drawRect(child.getLeft()-mDividerHeight,child.getTop()-mDividerHeight,child.getRight()+mDividerHeight,child.getTop(),mPaint);
                        }
                    }
                }
            }else{
                //正常绘制
                //判断是否是第一行
                if(i3 == 0){
                    //是第一行不绘制Top
                    //判断是否不是第一列
                    if(spanIndex!=0){
                        //如果不是第一列，则需要绘制Left,且Left高度不应超过top
                        c.drawRect(child.getLeft()-mDividerHeight, child.getTop(), child.getLeft(), child.getBottom(), mPaint);
                    }
                }
                //判断是否是第一列
                else if(spanIndex == 0){
                    //是第一列则不绘制Left,但是需要绘制Top
                    c.drawRect(child.getLeft(),child.getTop()-mDividerHeight,child.getRight(),child.getTop(),mPaint);
                }else{
                    //既不是第一行，也不是第一列,则即需要绘制Top，也需要绘制Left
                    c.drawRect(child.getLeft()-mDividerHeight, child.getTop()-mDividerHeight, child.getLeft(), child.getBottom(), mPaint);
                    c.drawRect(child.getLeft(),child.getTop()-mDividerHeight,child.getRight(),child.getTop(),mPaint);
                }
            }
        }
    }


    /**
     * 设置是否需要设置Grid的边界，默认是不设置边界
     * @param drawGridBorder
     */
    public void setDrawGridBorder(Boolean drawGridBorder) {
        isDrawGridBorder = drawGridBorder;
    }

    /**
     * 设置只绘制spanSize为drawGridSpanSizeBorder的item的分割线，
     * 当isDrawGridBorder == true时有效
     * @param spanSize
     */
    public void setDrawGridSpanSizeBorder( int spanSize){
        drawGridSpanSizeBorder = spanSize;
    }
}
