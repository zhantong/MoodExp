package org.graduation.slide;

import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.graduation.R;
import org.graduation.collector.AudioCollector;
import org.graduation.collector.ContactCollector;
import org.graduation.collector.GpsCollector;
import org.graduation.collector.ICollector;
import org.graduation.collector.WifiCollector;

import java.util.ArrayList;
import java.util.List;


public class FirstSlide extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.firstslide, container, false);





        return v;
    }
}