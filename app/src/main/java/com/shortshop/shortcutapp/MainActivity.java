package com.shortshop.shortcutapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends FragmentActivity implements Calling_add {

    Call<ResponseBody> serviceData;
    Retrofit retrofit;
    RestAPI service;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragments = new Vector<>();
    private PagerAdapter mPagerAdapter;
    private String[] TITLES;
    private ArrayList<TextView> tab_titles_list = new ArrayList<>();
    private static Context con;
    public static String resp;
    public static final String BASE_URL = "http://www.shortcut-app.com/shortcutapp/";

    public AdView mAdView;
    public static InterstitialAd mInterstitialAd;
    public static AdRequest inter_adRequest;

    public static boolean justOpened=true;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.mainview);
        activity = this;
        con= MainActivity.this;
        mViewPager = (ViewPager) findViewById(R.id.mainview_pager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        if(isConnectingToInternet()) {
            initRetrofit(BASE_URL);
            getResponse();
        }else {
            Toast.makeText(MainActivity.this, "Internet Connection Error", Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Shortcut App");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        initAD();
    }

    private void initAD() {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are your sure you want to exit?");
        builder.setPositiveButton("LEAVE",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setNegativeButton("STAY",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog = builder.create();
    }


    protected static boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public void initRetrofit(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url).build();
        service = retrofit.create(RestAPI.class);
    }


    @Override
    public void onBackPressed() {
        dialog.show();
    }

    void getResponse() {
        serviceData = service.getUrlData("short.php");
        serviceData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                     resp = response.body().string();
                    JSONObject jsonObject = new JSONObject(resp);
                    Log.e("resp",""+jsonObject.toString());
                    TITLES = new String[jsonObject.getJSONArray("titles").length()];
                    for(int i=0;i<TITLES.length;i++) {
                        TITLES[i] = jsonObject.getJSONArray("titles").getString(i);
                        fragments.add( HomeFrag.newInstance(TITLES[i],i));
                    }
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                    MainActivity.this.mPagerAdapter = new PagerAdapter(MainActivity.this.getSupportFragmentManager(), fragments);
                    MainActivity.this.mViewPager = (ViewPager) MainActivity.this.findViewById(R.id.mainview_pager);
                    MainActivity.this.mViewPager.setAdapter(MainActivity.this.mPagerAdapter);
                    tabLayout.setupWithViewPager(MainActivity.this.mViewPager);
                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                                           @Override
                                                           public void onTabSelected(TabLayout.Tab tab) {
                                                               mViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
                                                               for (int i = 0; i < TITLES.length; i++) {
                                                                   if (i == tabLayout.getSelectedTabPosition()) {
                                                                       tab_titles_list.get(i).setTypeface(Typeface.DEFAULT_BOLD);
                                                                       tab_titles_list.get(i).setTextColor(getResources().getColor(android.R.color.black));
                                                                   } else {
                                                                       tab_titles_list.get(i).setTextColor(getResources().getColor(android.R.color.darker_gray));
                                                                       tab_titles_list.get(i).setTypeface(Typeface.DEFAULT);
                                                                   }
                                                               }
                                                           }

                                                           @Override
                                                           public void onTabUnselected(TabLayout.Tab tab) {
                                                           }

                                                           @Override

                                                           public void onTabReselected(TabLayout.Tab tab) {
                                                           }
                                                       }

                    );
                    mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    for (int i = 0; i < TITLES.length; i++) {
                        View tabview = getLayoutInflater().inflate(R.layout.item_tab, null);
                        TextView txt_title = (TextView) tabview.findViewById(R.id.tab_text);
                        txt_title.setText(TITLES[i]);
                        if (i == 0) {
                            txt_title.setTypeface(Typeface.DEFAULT_BOLD);
                            txt_title.setTextColor(getResources().getColor(android.R.color.black));
                        } else {
                            txt_title.setTypeface(Typeface.DEFAULT);
                            txt_title.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }
                        tab_titles_list.add(txt_title);
                        txt_title.setGravity(Gravity.CENTER);
                        tabLayout.getTabAt(i).setCustomView(tabview);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
        initializeInterstitialAdds();
        my_adds();
    }

   /* protected void initializeInterstitialAdds() {
         mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1671845713125648/2547098612");
        AdRequest inter_adRequest;
        if (loc_available) {
            inter_adRequest = new AdRequest.Builder()
                    .setLocation(location)
                    .build();
        } else {
            inter_adRequest = new AdRequest.Builder()
                    .addTestDevice("FE40329C00A37F6ACEBEB8DDB5140630")
                    .build();
        }
        mInterstitialAd.loadAd(inter_adRequest);
    }
    public  void initializeInterstitialAdds() {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-9862631671335648/2333635418");
        inter_adRequest = new AdRequest.Builder()
                .addTestDevice("2C14496EAB6A6D68D5DE6F8A124A53F8")
                .build();
        mInterstitialAd.loadAd(inter_adRequest);
    }
    public void my_adds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1671845713125648~8593632218");
         mAdView = (AdView) findViewById(R.id.adView);
        AdRequest banner_adRequest;
        if (loc_available) {
            banner_adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .setLocation(location)
                    .build();
        } else {
            banner_adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("FE40329C00A37F6ACEBEB8DDB5140630")
                    .build();
        }
        mAdView.loadAd(banner_adRequest);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
//        nightimage.setVisibility(nightmodeEnabled ? View.VISIBLE : View.GONE);
//        Log.e("bool",""+justOpened);
        if (justOpened) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            initializeInterstitialAdds();
            justOpened = false;
        }
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }*/


    public  void initializeInterstitialAdds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3327059441830141/4978511712");
        inter_adRequest = new AdRequest.Builder()
                .addTestDevice("2C14496EAB6A6D68D5DE6F8A124A53F8")
                .build();
        mInterstitialAd.loadAd(inter_adRequest);
    }

    public void my_adds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1671845713125648~8593632218");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest banner_adRequest;
       /* if (loc_available) {
            banner_adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .setLocation(location)
                    .build();
        } else*/ {
            banner_adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("FE40329C00A37F6ACEBEB8DDB5140630")
                    .build();
        }
        mAdView.loadAd(banner_adRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

       /* if (!justOpened) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            initializeInterstitialAdds();

        }
        justOpened = false;*/
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }


    @Override
    public void callingAdd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        initializeInterstitialAdds();
    }
}
