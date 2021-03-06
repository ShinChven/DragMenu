package com.ShinChven.dragmenu.app;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.GitHub.ShinChven.DragMenu.DragMenu;
import com.ikimuhendis.ldrawer.DrawerArrowDrawable;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    public static final String DEMO_URL = "http://www.atlassc.net/";
    private BaseAdapter mGridAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return strs.length;
        }

        @Override
        public Object getItem(int position) {
            return strs[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_grid, null);
            }
            return convertView;
        }
    };
    private String[] strs = new String[]{
            "alpha", "beta", "jason", "jon", "atlas", "cloud", "default", "menu"
    };
    private Toolbar mToolBar;
    private DragMenu mDragMenu;
    private WebView mWebView;
    private SwipeRefreshLayout mSwipe;
    private ListView mMenuListView;
    private ListView mList;
    private BaseAdapter mAdaper = new BaseAdapter() {
        @Override
        public int getCount() {
            return 25;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_list, null);
            }
            return convertView;
        }
    };
    private DrawerArrowDrawable mHomeArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        setupDragMenu();
        setupContent();

    }

    @SuppressLint("ResourceAsColor")
    private void setupActionBar() {
        mToolBar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHomeArrow = new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl() {
                return false;
            }
        };
        mHomeArrow.setColor(R.color.material_blue_grey_950);
        mToolBar.setNavigationIcon(mHomeArrow);
    }

    private void setupContent() {
        mSwipe = ((SwipeRefreshLayout) findViewById(R.id.swipe_layout));
        mSwipe.setOnRefreshListener(this);
        mWebView = ((WebView) findViewById(R.id.web_view));
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mSwipe.setRefreshing(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mSwipe.setRefreshing(true);
            }
        };
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(DEMO_URL);
//        mList = ((ListView) findViewById(R.id.main_list));
//        mList.setAdapter(mAdaper);
    }

    private void setupDragMenu() {
        mMenuListView = (ListView) findViewById(R.id.lv_menu);
        mMenuListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs
        ));

        mMenuListView.setOnItemClickListener(this);


        this.mDragMenu = (DragMenu) findViewById(R.id.dragMenu);
        this.mDragMenu.setTransformEnabled(true);
        this.mDragMenu.setDragListener(new DragMenu.DragListener() {
            @Override
            public void onOpen() {

            }

            @Override
            public void onClose() {

            }

            @Override
            public void onDrag(float percent) {
                mHomeArrow.setProgress(percent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (mDragMenu.isTransformEnabled()) {
                mDragMenu.setTransformEnabled(false);
                Toast.makeText(this, "transforming disabled", Toast.LENGTH_SHORT).show();
            } else {
                mDragMenu.setTransformEnabled(true);
                Toast.makeText(this, "transforming", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == android.R.id.home) {
            if (mDragMenu.getDragStatus() == DragMenu.DragStatus.Open) {
                mDragMenu.closeMenu();
            } else if (mDragMenu.getDragStatus() == DragMenu.DragStatus.Close) {
                mDragMenu.openMenu();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        try {
            mWebView.loadUrl(DEMO_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mDragMenu.closeMenu();
    }
}
