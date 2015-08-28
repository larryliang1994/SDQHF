// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Memo$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Memo> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493047, "field 'lv_memo'");
    target.lv_memo = finder.castView(view, 2131493047, "field 'lv_memo'");
    view = finder.findRequiredView(source, 2131493048, "field 'tv_empty'");
    target.tv_empty = finder.castView(view, 2131493048, "field 'tv_empty'");
    view = finder.findRequiredView(source, 2131493045, "field 'tb_memo'");
    target.tb_memo = finder.castView(view, 2131493045, "field 'tb_memo'");
    view = finder.findRequiredView(source, 2131493049, "field 'nv_memo'");
    target.nv_memo = finder.castView(view, 2131493049, "field 'nv_memo'");
    view = finder.findRequiredView(source, 2131493044, "field 'dw_memo'");
    target.dw_memo = finder.castView(view, 2131493044, "field 'dw_memo'");
  }

  @Override public void unbind(T target) {
    target.lv_memo = null;
    target.tv_empty = null;
    target.tb_memo = null;
    target.nv_memo = null;
    target.dw_memo = null;
  }
}
