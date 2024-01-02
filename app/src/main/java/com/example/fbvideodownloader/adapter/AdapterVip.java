package com.example.fbvideodownloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.fbvideodownloader.R;
import com.example.fbvideodownloader.model.Vipmodel;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterVip extends BaseAdapter {
    private Context context;
    private List<Vipmodel> list;

    public AdapterVip(Context context, List<Vipmodel> list) {
        this.context = context;
        this.list = list;
    }

    int selectedItem = -1;

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_vip, null);
        }
        TextView txtPrice = convertView.findViewById(R.id.tv_price_time);
        TextView txtPriceDay = convertView.findViewById(R.id.tv_price_day);
        TextView txtType = convertView.findViewById(R.id.tv_type);
        LinearLayout layout = convertView.findViewById(R.id.item);
        ImageView imgCheckBox = convertView.findViewById(R.id.img_checkbox);
        //
        if (selectedItem == position) {
            layout.setBackgroundResource(R.drawable.boder_item_vip);
            imgCheckBox.setImageResource(R.drawable.checkbox_true);
        } else {
            layout.setBackgroundResource(R.drawable.boder_popumenu);
            imgCheckBox.setImageResource(R.drawable.checkbox_false);
        }
        //
        Vipmodel vipModel = list.get(position);
        txtPrice.setText(formatDouble(vipModel.getPrice())+"/ "+vipModel.getTime());
        txtPriceDay.setText("Bằng "+convertCurrency(vipModel.getPrice(),vipModel.getTypeTime())+"đ/ Ngày");
        if(vipModel.getTypeTime()==7){
            txtType.setText("Hàng tuần");
            txtPriceDay.setVisibility(View.VISIBLE);
        }else if(vipModel.getTypeTime()==30){
            txtType.setText("Hàng Tháng");
            txtPriceDay.setVisibility(View.VISIBLE);
        }else if(vipModel.getTypeTime()==365){
            txtType.setText("Hàng Năm");
            txtPriceDay.setVisibility(View.VISIBLE);
        }else if(vipModel.getTypeTime()==36500){
            txtType.setText("Suốt đời");
            txtPriceDay.setVisibility(View.GONE);
        }
        return convertView;
    }
    private static String formatDouble(double number) {
        DecimalFormat decimalFormat;
        if (number >= 1000000) {
            decimalFormat = new DecimalFormat("#,##0.###đ");
        } else if (number >= 1000) {
            decimalFormat = new DecimalFormat("#,##0.###đ");
        } else {
            decimalFormat = new DecimalFormat("#,##đ");
        }
        return decimalFormat.format(number);
    }
    private static long convertCurrency(double amountPerWeek, int daysPerWeek) {
        double result = amountPerWeek / daysPerWeek;
        return Math.round(result);
    }
}
