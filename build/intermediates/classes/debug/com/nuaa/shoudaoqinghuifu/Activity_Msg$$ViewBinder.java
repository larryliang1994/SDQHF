// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Msg$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Msg> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493058, "field 'tv_empty'");
    target.tv_empty = finder.castView(view, 2131493058, "field 'tv_empty'");
    view = finder.findRequiredView(source, 2131493057, "field 'lv_msg'");
    target.lv_msg = finder.castView(view, 2131493057, "field 'lv_msg'");
    view = finder.findRequiredView(source, 2131493055, "field 'tb_msg'");
    target.tb_msg = finder.castView(view, 2131493055, "field 'tb_msg'");
    view = finder.findRequiredView(source, 2131493059, "field 'nv_msg'");
    target.nv_msg = finder.castView(view, 2131493059, "field 'nv_msg'");
    view = finder.findRequiredView(source, 2131493054, "field 'dw_msg'");
    target.dw_msg = finder.castView(view, 2131493054, "field 'dw_msg'");
  }

  @Override public void unbind(T target) {
    target.tv_empty = null;
    target.lv_msg = null;
    target.tb_msg = null;
    target.nv_msg = null;
    target.dw_msg = null;
  }
}
