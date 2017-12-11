package com.jiangzg.project.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Fragment的基类
 */
public abstract class BaseFragment<T> extends Fragment {

    public BaseActivity mActivity;
    public BaseFragment mFragment;
    public FragmentManager mFragmentManager;
    public View rootView;
    private Unbinder unbinder;

    /* 获取fragment实例demo */
    private static BaseFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(BaseFragment.class, bundle);
    }

    /**
     * 初始layout
     */
    protected abstract int initObj(Bundle data);

    /**
     * 实例化View/设置监听器
     */
    protected abstract void initView(@Nullable Bundle state);

    /**
     * 获取数据,最好开线程
     */
    protected abstract void initData(Bundle state);

    @Override
    public void onAttach(Context context) {
        mFragment = this;
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            mActivity = (BaseActivity) context;
            mFragmentManager = mActivity.getSupportFragmentManager();
        }
        initTransAnim(this);
    }

    /* Activity中的onAttachFragment执行完后会执行,相当于onCreate */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);// Fragment与ActionBar和MenuItem集成
    }

    /* 在这里返回绑定并View,从stack返回的时候也是先执行这个方法,相当于onStart */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            int layoutId = initObj(getArguments()); // 取出Bundle
            rootView = inflater.inflate(layoutId, container, false);
            unbinder = ButterKnife.bind(mFragment, rootView);
        }
        return rootView;
    }

    /* 一般在这里进行控件的实例化,加载监听器, 参数view就是fragment的layout */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
    }

    /* activity的onCreate执行完成后才会调用 */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData(savedInstanceState);
    }

    /*Fragment中的布局被移除时调用*/
    @Override
    public void onDestroyView() {
        rootView = getView();
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * @return 获取当前类(影响性能, 所以需要被动获取)
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getCls() {
        Type type = this.getClass().getGenericSuperclass();
        return (Class<T>) (((ParameterizedType) (type)).getActualTypeArguments()[0]);
    }

    /* 反射生成对象实例 */
    protected static <T> T newInstance(Class<T> clz, Bundle args) {
        T fragment = null;
        try {
            // 获取名为setArguments的函数
            Method setArguments = clz.getMethod("setArguments", Bundle.class);
            fragment = clz.newInstance(); // 走的无参构造函数
            if (args == null) { // 往Bundle里传值，这里是子类的类名
                args = new Bundle();
            }
            setArguments.invoke(fragment, args); // 执行setArguments方法
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    /* 过渡动画 */
    private void initTransAnim(Fragment fragment) {
        // 只要进的动画就好，出的有时候执行不完全会bug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionSet trans = new TransitionSet();
            trans.setOrdering(TransitionSet.ORDERING_TOGETHER)
                    .addTransition(new Fade(Fade.OUT))
                    .addTransition(new ChangeBounds())
                    .addTransition(new Fade(Fade.IN));
            // 压栈
            fragment.setEnterTransition(trans);
            // fragment.setExitTransition(trans);
            // 弹栈
            fragment.setReenterTransition(trans);
            // fragment.setReturnTransition(trans);
        }
    }

}
