package com.ben.qqnavigationdemo;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
//import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.ben.qqnavigationdemo.fragment.ContactsFragment;
import com.ben.qqnavigationdemo.fragment.MessageFragment;
import com.ben.qqnavigationdemo.fragment.StarFragment;
import com.ben.qqnavigationdemo.view.CircleImageView;
import com.ben.qqnavigationdemo.view.FragmentTabHost;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private DrawerLayout mDrawer;
    private FragmentTabHost mTabHost;
    private FrameLayout mTabContent;
    private View mTabView;

    private String[] mTabTexts;

    private int[] mTabIcons = new int[]{
            R.drawable.selector_nvg_message,
            R.drawable.selector_nvg_contacts,
            R.drawable.selector_nvg_star};

    private TextView mTvTitle;
    private ImageView mIvAdd;
    private TextView mTvAdd;
    private TextView mTvMore;
    private RelativeLayout mRlTitle;
    private RelativeLayout mRlMenu;
    private ColorShades mColorShades;
    private LinearLayout mLlContentMain;
    private CircleImageView mCivHead;
    private TextView mTvMessgeCount;
    private TextView mTvContactsCount;
    private TextView mTvStarCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRlMenu = (RelativeLayout) findViewById(R.id.rl_menu);
        mLlContentMain = (LinearLayout) findViewById(R.id.ll_content_main);
        mRlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mCivHead = (CircleImageView) findViewById(R.id.civ_head);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mTvMore = (TextView) findViewById(R.id.tv_more);

        mTvAdd.setVisibility(View.GONE);
        mTvMore.setVisibility(View.GONE);

        //底部导航设置
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //关联主布局
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        //默认设置选中第一个
        mTabHost.setCurrentTab(0);
        //去掉在版本中的横线,FragmentTabHost在低版本中,每个Tab之间会有条横线
        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);

        initBottomNavigationView();
    }

    private void initBottomNavigationView() {
        mTabTexts = getResources().getStringArray(R.array.tab_texts);

        for (int i = 0; i < mTabTexts.length; i++) {
            mTabView = View.inflate(this, R.layout.layout_item_tab, null);
            ((TextView) mTabView.findViewById(R.id.tv_tab_text)).setText(mTabTexts[i]);
            ((ImageView) mTabView.findViewById(R.id.iv_tab_icon)).setImageResource(mTabIcons[i]);

            //创建TabSpec
            TabHost.TabSpec messageTabSpec = mTabHost.newTabSpec(mTabTexts[i]).setIndicator(mTabView);

            Bundle bundle = new Bundle();
            bundle.putString(TAG, mTabTexts[i]);
            switch (i) {
                case 0:
                    mTvMessgeCount = (TextView) mTabView.findViewById(R.id.tv_message_count);
                    mTvMessgeCount.setText("99");
                    mTabHost.addTab(messageTabSpec, MessageFragment.class, bundle);
                    break;
                case 1:
                    mTvContactsCount = (TextView) mTabView.findViewById(R.id.tv_message_count);
                    mTvContactsCount.setText("8");
                    mTabHost.addTab(messageTabSpec, ContactsFragment.class, bundle);
                    break;
                case 2:
                    mTvStarCount = (TextView) mTabView.findViewById(R.id.tv_message_count);
                    mTvStarCount.setText("");
                    mTabHost.addTab(messageTabSpec, StarFragment.class, bundle);
                    break;
            }
        }
    }

    private void initData() {

    }

    private void initEvent() {
        mCivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.openDrawer(GravityCompat.START);
                }
            }
        });

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.i(TAG, "onTabChanged: tabId -- " + tabId);
                mTvTitle.setText(tabId);
                mIvAdd.setVisibility(mTabTexts[0].equals(tabId) ? View.VISIBLE : View.GONE);
                mTvAdd.setVisibility(mTabTexts[1].equals(tabId) ? View.VISIBLE : View.GONE);
                mTvMore.setVisibility(mTabTexts[2].equals(tabId) ? View.VISIBLE : View.GONE);
            }
        });

        mColorShades = new ColorShades();

        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //设置主布局随菜单滑动而滑动
                int drawerViewWidth = drawerView.getWidth();
                mLlContentMain.setTranslationX(drawerViewWidth * slideOffset);

                //设置控件最先出现的位置
                double padingLeft = drawerViewWidth * (1 - 0.618) * (1 - slideOffset);
                mRlMenu.setPadding((int) padingLeft, 0, 0, 0);

                //设置Title颜色渐变
                mColorShades.setFromColor("#001AA7F2")
                        .setToColor(Color.WHITE)
                        .setShade(slideOffset);
                mRlTitle.setBackgroundColor(mColorShades.generate());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;
    }
}
