package com.example.fbvideodownloader.fragment;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.fbvideodownloader.R;


public class Cach2Fragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_cach2, container, false);
        TextView txtLabel = v.findViewById(R.id.txtlabel_cach2);
        TextPaint paint = txtLabel.getPaint();
        float width = paint.measureText(txtLabel.getText().toString());

        Shader textShader = new LinearGradient(0, 0, width, txtLabel.getTextSize(),
                new int[]{
                        Color.parseColor("#0066FF"),
                        Color.parseColor("#0092E4"),
                        Color.parseColor("#00C2FF"),
                }, null, Shader.TileMode.CLAMP);
        txtLabel.getPaint().setShader(textShader);
        v.findViewById(R.id.btn_DaHieu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return v;
    }
}