package com.kyler.mland.egg;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kyler.mland.egg.activities.planes.USAPlanes;

public class HomeAdapter extends FragmentStateAdapter {

    public HomeAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new USAPlanes();
            case 1:
                return new USAPlanes();
            case 2:
                return new USAPlanes();
            case 3:
                return new USAPlanes();
            case 4:
                return new USAPlanes();
            case 5:
                return new USAPlanes();
            case 6:
                return new USAPlanes();
            case 7:
                return new USAPlanes();
            case 8:
                return new USAPlanes();
            case 9:
                return new USAPlanes();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 10;
    }
}