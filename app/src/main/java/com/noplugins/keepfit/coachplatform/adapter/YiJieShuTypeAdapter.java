package com.noplugins.keepfit.coachplatform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.noplugins.keepfit.coachplatform.R;
import com.noplugins.keepfit.coachplatform.bean.ClassDateBean;
import com.noplugins.keepfit.coachplatform.fragment.ScheduleFragment;

import java.util.List;

public class YiJieShuTypeAdapter extends BaseAdapter {
    private ScheduleFragment scheduleFragment;
    private LayoutInflater inflater;

    private List<String> list;

    public YiJieShuTypeAdapter(List<String> mlist, ScheduleFragment m_scheduleFragment) {
        this.scheduleFragment = m_scheduleFragment;
        this.list = mlist;
        this.inflater = LayoutInflater.from(scheduleFragment.getContext());
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final viewHolder holder;
        if (convertView == null) {
            holder = new viewHolder();
            convertView = inflater.inflate(R.layout.weijieshu_item, null);
            holder.status_tv = convertView.findViewById(R.id.status_tv);
            holder.changguan_name = convertView.findViewById(R.id.changguan_name);
            holder.time_tv = convertView.findViewById(R.id.time_tv);
            holder.class_type = convertView.findViewById(R.id.class_type);
            holder.status_img = convertView.findViewById(R.id.status_img);
            holder.type_icon_tv = convertView.findViewById(R.id.type_icon_tv);
            holder.type_icon_bg = convertView.findViewById(R.id.type_icon_bg);
            holder.people_number_tv = convertView.findViewById(R.id.people_number_tv);
            holder.button_tv = convertView.findViewById(R.id.button_tv);
            holder.button_bg = convertView.findViewById(R.id.button_bg);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }


        holder.changguan_name.setText(list.get(position));


        return convertView;
    }


    private class viewHolder {
        public TextView status_tv, changguan_name, time_tv, class_type, type_icon_tv, people_number_tv, button_tv;
        public ImageView status_img;
        public LinearLayout button_bg, type_icon_bg;
    }
}