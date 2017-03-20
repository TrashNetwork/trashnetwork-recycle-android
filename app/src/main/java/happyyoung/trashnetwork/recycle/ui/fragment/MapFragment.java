package happyyoung.trashnetwork.recycle.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import happyyoung.trashnetwork.recycle.R;

public class MapFragment extends Fragment {
    private View rootView;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Context context) {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

}
