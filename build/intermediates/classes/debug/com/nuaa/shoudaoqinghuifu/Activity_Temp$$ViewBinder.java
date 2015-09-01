// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Temp$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Temp> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493083, "field 'gv_temp'");
    target.gv_temp = finder.castView(view, 2131493083, "field 'gv_temp'");
    view = finder.findRequiredView(source, 2131493084, "field 'tv_empty'");
    target.tv_empty = finder.castView(view, 2131493084, "field 'tv_empty'");
    view = finder.findRequiredView(source, 2131493081, "field 'tb_temp'");
    target.tb_temp = finder.castView(view, 2131493081, "field 'tb_temp'");
    view = finder.findRequiredView(source, 2131493085, "field 'nv_temp'");
    target.nv_temp = finder.castView(view, 2131493085, "field 'nv_temp'");
    view = finder.findRequiredView(source, 2131493080, "field 'dw_temp'");
    target.dw_temp = finder.castView(view, 2131493080, "field 'dw_temp'");
  }

  @Override public void unbind(T target) {
    target.gv_temp = null;
    target.tv_empty = null;
    target.tb_temp = null;
    target.nv_temp = null;
    target.dw_temp = null;
  }
}
