package id.novanto.myapplication.Base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Novanto on 27-May-17.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements ViewBehavior {
    private boolean readExtrasSuper;

    private Bundle bundle;

    private T presenter;

    private Unbinder unbinder;

    public abstract int layoutResID();

    protected int menuResID() {
        return -1;
    }

    protected abstract T providePresenter();

    protected T presenter() {
        if (this.presenter == null) {
            this.presenter = providePresenter();
            this.presenter.setView(this);
        }

        return this.presenter;
    }

    protected void readExtras(Bundle extras) {
        readExtrasSuper = true;

        if (extras != null) {
            this.bundle = extras;
        }
    }

    protected Bundle getExtras() {
        return this.bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = savedInstanceState;
        if (data == null) {
            data = getArguments();
        }

        readExtras(data);

        if (!readExtrasSuper) {
            throw new RuntimeException("Need to call super read extras");
        }

        if (menuResID() > 0) {
            setHasOptionsMenu(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutResID(), container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuResID = menuResID();
        boolean hasOptionMenu = (menuResID > 0);

        if (hasOptionMenu) {
            inflater.inflate(menuResID, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (this.bundle != null) {
            outState.putAll(this.bundle);
        }

        super.onSaveInstanceState(outState);
    }

    public void startActivity(Class destination) {
        getActivity().startActivity(new Intent(getActivity(), destination));
    }

}
