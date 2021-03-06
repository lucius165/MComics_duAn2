package com.champhay.mcomics.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.champhay.Model.Util;
import com.champhay.Model.custom.adapter.RecycleViewCustomAdapter;
import com.champhay.Model.custom.component.NavigationDrawer;
import com.champhay.Model.handler.backgroundtask.CheckInternet;
import com.champhay.Model.handler.backgroundtask.LoadJsonInBackground;
import com.champhay.Model.handler.backgroundtask.ParserJSON;
import com.champhay.Model.handler.eventlistener.DownloadEvent;
import com.champhay.Model.handler.eventlistener.OnViewCreateCallback;
import com.champhay.Model.handler.social.Comics;
import com.champhay.Model.handler.social.ComicsKind;
import com.champhay.Model.handler.social.FacebookAPI;
import com.champhay.mcomics.R;

import org.json.JSONException;

import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * Created by HoangTP
 */

public class HomeActivity extends AppCompatActivity implements DownloadEvent {
    private NavigationDrawer navigationDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!CheckInternet.check(this)) {
            setContentView(R.layout.view_connect_fail);
            return;
        }
        setContentView(R.layout.activity_home);

        createLoadingFragment();
        startLoadData();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.champhay.mcomics",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception ignored) {
//            Log.e("here", "error");
//        }
    }

    //button search
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action, menu);
        return true;
    }

    public void startLoadData() {
        LoadJsonInBackground backgroundTask = new LoadJsonInBackground();
        backgroundTask.setOnFinishEvent(this);
        backgroundTask.execute(Util.BASE_URL + "/comicsApi.php/getComicsTop");
    }

    @Override
    public void onLoadFinish(final String string) {
        FragmentCreator fragment = FragmentCreator.getFragment(R.layout.view_navigation, "main");
        fragment.setOnViewCreateCallback((view, tag) -> {
            try {
                createMainFragment(view, string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    public void createLoadingFragment() {
        FragmentCreator fragment = FragmentCreator.getFragment(R.layout.fragment_loading, "loading");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    public void createMainFragment(View view, String string) throws JSONException {
        navigationDrawer = new NavigationDrawer(this, R.layout.fragment_main, (ViewGroup) view);

        ArrayList<Comics> comicsArray = new ParserJSON().getComicArray(string);
        createRecyclerView(view, comicsArray);
        LoadJsonInBackground backgroundTask = new LoadJsonInBackground();
        backgroundTask.setOnFinishEvent(string1 -> {
            try {
                showKindListItem(string1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        backgroundTask.execute(Util.BASE_URL + "/comicsApi.php/getListKind");
    }

    public void createRecyclerView(View view, ArrayList<Comics> comicsArray) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewList);
        recyclerView.setLayoutManager(layoutManager);
        RecycleViewCustomAdapter adapter = new RecycleViewCustomAdapter(comicsArray);
        recyclerView.setAdapter(adapter);
    }

    public void showKindListItem(String string) throws JSONException {
        LinearLayout layout = findViewById(R.id.linear_kind);
        final ArrayList<ComicsKind> comicsKindArray = new ParserJSON().getComicKindArray(string);
        for (int x = 0; x < comicsKindArray.size(); x++) {
            View view = (LayoutInflater.from(this)).inflate(R.layout.view_kind_list_item, null, false);
            ((TextView) view.findViewById(R.id.id)).setText(comicsKindArray.get(x).getId() + "");
            ((TextView) view.findViewById(R.id.text)).setText(comicsKindArray.get(x).getKind());
            final int finalX = x;
            view.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ComicsCategoryActivity.class);
                intent.putExtra("id", comicsKindArray.get(finalX).getId());
                startActivity(intent);
            });
            layout.addView(view, x);
        }
    }

    public static class FragmentCreator extends Fragment {
        private OnViewCreateCallback onViewCreateCallback;
        static int layout = R.layout.activity_home;
        static String tag;

        public static FragmentCreator getFragment(int layoutInt, String tagStr) {
            tag = tagStr;
            layout = layoutInt;
            return new FragmentCreator();
        }

        public void setOnViewCreateCallback(OnViewCreateCallback onViewCreateCallback) {
            this.onViewCreateCallback = onViewCreateCallback;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(layout, container, false);
            if (onViewCreateCallback != null) {
                onViewCreateCallback.OnViewCreate(view, tag);
            }
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        navigationDrawer.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
