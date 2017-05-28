package id.novanto.myapplication.Base;

import android.content.Context;

/**
 * Created by Novanto on 27-May-17.
 */

public abstract class BasePresenter <V extends ViewBehavior> {

    public V view;

    private Context context;

    public BasePresenter(Context context) {
        this.context = context;
    }

    public void setView(V view) {
        this.view = view;
    }

    protected Context getContext() {
        return this.context;
    }
}
