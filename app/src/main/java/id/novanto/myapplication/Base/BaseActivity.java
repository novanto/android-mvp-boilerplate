package id.novanto.myapplication.Base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import id.novanto.myapplication.R;

/**
 * Created by Novanto on 27-May-17.
 */

public abstract class BaseActivity<V extends BaseFragment, P extends BasePresenter> extends AppCompatActivity implements ViewBehavior {

    private V viewLayer;

    private P presenter;

    private Toolbar toolbar;

    private Bundle bundle;

    private Menu menu;

    private int containerResId = R.id.container;

    private Unbinder unbinder;

    // region Abstractions

    protected int layoutResID() {
        return R.layout.activity_fragment;
    }

    protected int menuResID() {
        return -1;
    }

    protected String title() {
        return null;
    }

    protected boolean showBackButton() {
        return false;
    }

    protected int resourceForUpIndicator() {
        return -1;
    }

    protected abstract P providePresenter();

    protected P presenter() {
        if (this.presenter == null) {
            this.presenter = providePresenter();
            this.presenter.setView(this);
        }

        return this.presenter;
    }

    // endregion

    //region Lifecycle

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        int layoutResID = layoutResID();
        if (layoutResID > 0) {
            setContentView(layoutResID);
            unbinder = ButterKnife.bind(this);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String title = title();
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        if (this.toolbar != null) {
            setSupportActionBar(this.toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (showBackButton()) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);

                int upResourceId = resourceForUpIndicator();
                if (upResourceId > -1) {
                    actionBar.setHomeAsUpIndicator(upResourceId);
                }
            }
        }

        this.bundle = savedInstanceState;
        if (this.bundle == null && getIntent() != null) {
            this.bundle = getIntent().getExtras();
        }

        Fragment containerFragment = getContainerFragment();
        if (containerFragment == null) {
            this.viewLayer = viewLayer(this.bundle);
            Fragment fragment = this.viewLayer;

            addFragment(fragment);
        }
        else {
            restoreFragment(containerFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (this.bundle != null) {
            outState.putAll(this.bundle);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuResID = menuResID();
        boolean hasOptionMenu = (menuResID > 0);

        if (hasOptionMenu) {
            getMenuInflater().inflate(menuResID, menu);
        }

        this.menu = menu;

        return hasOptionMenu || showBackButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    //endregion

    // region Fragments methods

    protected void restoreFragment(Fragment fragment) {
        this.viewLayer = (V) fragment;
    }

    protected Fragment getContainerFragment() {
        return getSupportFragmentManager().findFragmentById(this.containerResId);
    }

    protected void addFragment(Fragment viewLayer) {
        if (viewLayer != null) {
            getSupportFragmentManager().beginTransaction()
                    .add(this.containerResId, viewLayer)
                    .commit();
        }
    }

    protected abstract V viewLayer(Bundle bundle);

    public V getViewLayer() {
        return this.viewLayer;
    }

    // endregion

    //region Functionalitty

    public Bundle getBundle() {
        return this.bundle;
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public void hideToolbar() {
        if (getToolbar() != null) {
            getToolbar().setVisibility(View.GONE);
        } else {
            getSupportActionBar().hide();
        }
    }

    //endregion
}
