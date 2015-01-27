package com.ShinChven.dragmenu.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import com.GitHub.ShinChven.DragMenu.DragMenu;


public class MainActivity extends ActionBarActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolBar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(mToolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ((ListView) findViewById(R.id.lv_menu)).setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs
        ));

        ((GridView) findViewById(R.id.gv_content)).setAdapter(mGridAdapter);
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
}
