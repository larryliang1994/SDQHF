// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_AddTemp$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_AddTemp> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493008, "field 'edt_title'");
    target.edt_title = finder.castView(view, 2131493008, "field 'edt_title'");
    view = finder.findRequiredView(source, 2131493009, "field 'edt_content'");
    target.edt_content = finder.castView(view, 2131493009, "field 'edt_content'");
    view = finder.findRequiredView(source, 2131493007, "field 'tb_addtemp'");
    target.tb_addtemp = finder.castView(view, 2131493007, "field 'tb_addtemp'");
  }

  @Override public void unbind(T target) {
    target.edt_title = null;
    target.edt_content = null;
    target.tb_addtemp = null;
  }
}
