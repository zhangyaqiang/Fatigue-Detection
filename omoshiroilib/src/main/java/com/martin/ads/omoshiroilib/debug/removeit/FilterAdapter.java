package com.martin.ads.omoshiroilib.debug.removeit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;

import java.util.List;

/**
 * Created by why8222 on 2016/3/17.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    
    private List<FilterType> filterTypeList;
    private Context context;
    private int selected = 0;

    public FilterAdapter(Context context, List<FilterType> filterTypeList) {
        this.filterTypeList = filterTypeList;
        this.context = context;
    }

    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filter_item_layout,
                parent, false);
        FilterHolder viewHolder = new FilterHolder(view);
        viewHolder.thumbImage = (ImageView) view
                .findViewById(R.id.filter_thumb_image);
        viewHolder.filterName = (TextView) view
                .findViewById(R.id.filter_thumb_name);
        viewHolder.filterRoot = (FrameLayout)view
                .findViewById(R.id.filter_root);
        viewHolder.thumbSelected = (FrameLayout) view
                .findViewById(R.id.filter_thumb_selected);
        viewHolder.thumbSelected_bg = view.
                findViewById(R.id.filter_thumb_selected_bg);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterHolder holder,final int position) {
        final FilterType filterType=filterTypeList.get(position);
        holder.thumbImage.setImageBitmap(FilterResourceHelper.getFilterThumbFromFiles(context,filterType));
        holder.filterName.setText(FilterResourceHelper.getSimpleName(filterType));
//        holder.filterName.setBackgroundColor(context.getResources().getColor(
//                FilterTypeHelper.FilterType2Color(filters[position])));
        if(position == selected){
            holder.thumbSelected.setVisibility(View.VISIBLE);
//            holder.thumbSelected_bg.setBackgroundColor(context.getResources().getColor(
//                    FilterTypeHelper.FilterType2Color(filters[position])));
            holder.thumbSelected_bg.setAlpha(0.7f);
        }else {
            holder.thumbSelected.setVisibility(View.GONE);
        }

        holder.filterRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(selected == position)
                    return;
                int lastSelected = selected;
                selected = position;
                notifyItemChanged(lastSelected);
                notifyItemChanged(position);
                onFilterChangeListener.onFilterChanged(filterTypeList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterTypeList == null ? 0 : filterTypeList.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder {
        ImageView thumbImage;
        TextView filterName;
        FrameLayout thumbSelected;
        FrameLayout filterRoot;
        View thumbSelected_bg;

        public FilterHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnFilterChangeListener{
        void onFilterChanged(FilterType filterType);
    }

    private OnFilterChangeListener onFilterChangeListener;

    public void setOnFilterChangeListener(OnFilterChangeListener onFilterChangeListener){
        this.onFilterChangeListener = onFilterChangeListener;
    }
}
