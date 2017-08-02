package com.shortshop.shortcutapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;

public class BrowseFrag extends Activity {
    protected WebView webview;
    private ProgressBar progress;
    private Bundle extras;
    String name = "Shortcut App";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.browsefrag);
        webview = (WebView) findViewById(R.id.webview);
        progress = (ProgressBar) findViewById(R.id.progress);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webview.getSettings().setSavePassword(true);
        webview.getSettings().setSaveFormData(true);
        webview.getSettings().setEnableSmoothTransition(true);
        webview.getSettings().setSupportZoom(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setLightTouchEnabled(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                try {
                    Toast.makeText(BrowseFrag.this, "Internet Connection Error" + description, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            extras = getIntent().getExtras();
            if (MainActivity.isConnectingToInternet()) {
                progress.setVisibility(View.VISIBLE);
                webview.loadUrl(extras.getString("url"));
                name = (extras.getString("name")).replace("icons", "").replace("/", "").
                        replaceAll("\\d", "").replace(".png", "").replace(".jpg", "");
            } else {
                Toast.makeText(BrowseFrag.this, "Internet Connection Error", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }

        CookieManager.getInstance().setAcceptCookie(true);

        findViewById(R.id.rateus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });
        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.bookmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                createGoogleSearchShortcut(BrowseFrag.this);
                getImageResource();
            }
        });
        try {
            if (null != getIntent().getData()) {
                Log.e("shortcut", getIntent().getDataString());
                webview.loadUrl(getIntent().getDataString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getImageResource(){
        try {
            boolean identified=false;
            Field[] ID_Fields = R.drawable.class.getFields();
            int[] resArray = new int[ID_Fields.length];
            for (int i = 0; i < ID_Fields.length; i++) {
                try {
                    resArray[i] = ID_Fields[i].getInt(null);
                    String resource = getResources().getResourceEntryName(resArray[i]);
                    Log.e("image name is","<><>"+name);
                    if(resource.equalsIgnoreCase(name.replace("-",""))){
                        Log.e("identified ","<><>"+name);
                        identified = true;
                        createGoogleSearchShortcut(BrowseFrag.this,resArray[i]);
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if(!identified){
                String mDrawableName = "logo";
                int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
                createGoogleSearchShortcut(BrowseFrag.this,resID);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void createGoogleSearchShortcut(Context context,int image_tag) {
        try {
            String urlStr = String.format(extras.getString("url"), BuildConfig.APPLICATION_ID);
            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN, Uri.parse(urlStr), this, BrowseFrag.class);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, image_tag));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            context.sendBroadcast(intent);
            Toast.makeText(BrowseFrag.this, "Bookmarked on Home Screen", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }

        Calling_add calling_add = (Calling_add)MainActivity.activity;
        calling_add.callingAdd();

        /*if(MainActivity.mInterstitialAd.isLoaded()){
                MainActivity.mInterstitialAd.show();
        }*/
    }

//    public int getImageID(String name) {
//        int id = 0;
//        if (name.contains("amazon")) {
//            id = R.drawable.amazon;
//        } else if (name.contains("flipkart")) {
//            id = R.drawable.flipkart;
//        } else if (name.contains("snapdeal")) {
//            id = R.drawable.snapdeal;
//        } else if (name.contains("paytm")) {
//            id = R.drawable.paytm;
//        } else if (name.contains("shopclues")) {
//            id = R.drawable.shopclues;
//        } else if (name.contains("ebay")) {
//            id = R.drawable.ebay;
//        } else if (name.contains("myntra")) {
//            id = R.drawable.myntra;
//        } else if (name.contains("voonik")) {
//            id = R.drawable.voonik;
//        } else if (name.contains("cry")) {
//            id = R.drawable.firstcry;
//        } else if (name.contains("inox")) {
//            id = R.drawable.inox;
//        } else if (name.contains("book")) {
//            id = R.drawable.bookmyshow;
//        } else if (name.contains("carnivalcinemas")) {
//            id = R.drawable.carnivalcinemas;
//        } else if (name.contains("pvr")) {
//            id = R.drawable.pvr;
//        } else if (name.contains("big")) {
//            id = R.drawable.bigcinemas;
//        } else if (name.contains("zomato")) {
//            id = R.drawable.zomato;
//        } else if (name.contains("panda")) {
//            id = R.drawable.foodpanda;
//        } else if (name.contains("mcdonalds")) {
//            id = R.drawable.mcdonalds;
//        } else if (name.contains("domino")) {
//            id = R.drawable.dominos;
//        } else if (name.contains("yatra")) {
//            id = R.drawable.yatra;
//        } else if (name.contains("goibibo")) {
//            id = R.drawable.goibibo;
//        } else if (name.contains("red")) {
//            id = R.drawable.redbus;
//        } else if (name.contains("oyo")) {
//            id = R.drawable.oyo;
//        } else if (name.contains("makemytrip")) {
//            id = R.drawable.makemytrip;
//        } else if (name.contains("quicker")) {
//            id = R.drawable.quicker;
//        } else if (name.contains("olx")) {
//            id = R.drawable.olx;
//        }else {
//            id = R.mipmap.ic_launcher;
//        }
//        return id;
//    }
}
