package com.tone.dragandswipe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhaotong on 2016/5/25.
 */
public class DragAdapter extends RecyclerView.Adapter<DragAdapter.ItemViewHolder> {

    private Context context;
    private ItemTouchHelper mItemTouchHelper;
    private List<Channel> mItems = new ArrayList<>();
    private boolean isCanDrag = false;
    private boolean isItemCanMove = true;
    private Vibrator mVibrator;

    public DragAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback());
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setmItems(List<Channel> mItems) {
        this.mItems = mItems;
        notifyDataSetChanged();
    }

    public void addItem(Channel channel){
        mItems.add(channel);
        notifyItemInserted(mItems.size()-1);
    }

    public void setDragComplete() {
        isCanDrag = false;
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscribe_layout, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(mItems.get(position).getChannelName());
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isCanDrag && MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mItemTouchHelper.startDrag(holder);
                }
                return false;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isCanDrag = true;
                mVibrator.vibrate(50); //震动一下
                notifyDataSetChanged();
                if (dragListener!=null)
                    dragListener.onLongClick(v);
                return true;
            }
        });

        holder.handleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDismiss(holder.getAdapterPosition());
            }
        });
        boolean isShowDelete = isCanDrag && (mItems.get(position).getIsFixed() == 1);
        holder.handleView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
        if (mItems.get(position).getIsFixed() == 0 && isCanDrag) {
            holder.textView.setBackgroundResource(R.drawable.subscribe_press_bg);
        } else {
            holder.textView.setBackgroundResource(R.drawable.subscribe_normal_bg);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void onItemDismiss(int position) {
        if (dragListener!=null)
            dragListener.onItemRemoved(mItems.get(position));
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            Log.d("ItemTouchHelper", "--------------isLongPressDragEnabled");
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            Log.d("ItemTouchHelper", "--------------isItemViewSwipeEnabled");
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.d("ItemTouchHelper", "--------------getMovementFlags");
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                int position = viewHolder.getAdapterPosition();
                if (mItems.get(position).getIsFixed() == 0)
                    isItemCanMove = false;
                else
                    isItemCanMove = true;
                return makeMovementFlags(isItemCanMove == true ? dragFlags : 0, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d("ItemTouchHelper", "--------------onMove");

            int position = target.getAdapterPosition();
            if (mItems.get(position).getIsFixed() == 0)
                return false;
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("ItemTouchHelper", "--------------onSwiped");
            onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            Log.d("ItemTouchHelper", "--------------onChildDraw");
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Fade out the view as it is swiped out of the parent's bounds
                final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            Log.d("ItemTouchHelper", "--------------onSelectedChanged");
            // We only want the active item to change
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof ItemViewHolder) {
                    // Let the view holder know that this item is being moved or dragged
                    ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }


            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            Log.d("ItemTouchHelper", "--------------clearView");
            super.clearView(recyclerView, viewHolder);
            //侧滑
//            viewHolder.itemView.setAlpha(1.0f);

            if (viewHolder instanceof ItemViewHolder) {
                // Tell the view holder it's time to restore the idle state
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                itemViewHolder.onItemClear();
            }
        }


    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public final TextView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (TextView) itemView.findViewById(R.id.delete);
        }

        public void onItemSelected() {
            textView.setBackgroundColor(Color.LTGRAY);
        }

        public void onItemClear() {
            textView.setBackgroundResource(R.drawable.subscribe_normal_bg);
        }
    }


    private DragListener dragListener;

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    public interface DragListener<E>{
        void onLongClick(View view);
        void onItemRemoved(E e);
    }
}
