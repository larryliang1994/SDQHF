// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Preference$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Preference> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558678, "field 'pb'");
    target.pb = finder.castView(view, 2131558678, "field 'pb'");
    view = finder.findRequiredView(source, 2131558680, "field 'nv_preference'");
    target.nv_preference = finder.castView(view, 2131558680, "field 'nv_preference'");
    view = finder.findRequiredView(source, 2131558674, "field 'dw_preference'");
    target.dw_preference = finder.castView(view, 2131558674, "field 'dw_preference'");
    view = finder.findRequiredView(source, 2131558676, "field 'tb_preference'");
    target.tb_preference = finder.castView(view, 2131558676, "field 'tb_preference'");
    view = finder.findRequiredView(source, 2131558679, "field 'iv_expand'");
    target.iv_expand = finder.castView(view, 2131558679, "field 'iv_expand'");
  }

  @Override public void unbind(T target) {
    target.pb = null;
    target.nv_preference = null;
    target.dw_preference = null;
    target.tb_preference = null;
    target.iv_expand = null;
  }
}
