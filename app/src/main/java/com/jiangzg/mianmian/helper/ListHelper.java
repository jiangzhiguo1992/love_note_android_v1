package com.jiangzg.mianmian.helper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.mianmian.domain.BaseObj;

import java.util.List;

/**
 * Created by JZG on 2018/4/19.
 * ListHelper
 */
public class ListHelper {

    public static <T extends BaseObj> int findIndexInList(List<T> list, T obj) {
        if (list == null || list.size() <= 0) return -1;
        if (obj == null || obj.getId() == 0) return -1;
        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item == null) continue;
            if (((BaseObj) item).getId() == obj.getId()) {
                return i;
            }
        }
        return -1;
    }

    public static <A extends BaseQuickAdapter, T extends BaseObj> void removeIndexInAdapter(A adapter, T obj) {
        if (adapter == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexInList(data, obj);
        if (index < 0) return;
        adapter.remove(index);
    }

    public static <A extends BaseQuickAdapter, T extends BaseObj> void refreshIndexInAdapter(A adapter, T obj) {
        if (adapter == null) return;
        List data = adapter.getData();
        int index = ListHelper.findIndexInList(data, obj);
        if (index < 0) return;
        adapter.setData(index, obj);
    }

}
