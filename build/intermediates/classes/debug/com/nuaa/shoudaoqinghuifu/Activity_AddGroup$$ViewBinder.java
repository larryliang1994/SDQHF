// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_AddGroup$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_AddGroup> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558593, "field 'et_name'");
    target.et_name = finder.castView(view, 2131558593, "field 'et_name'");
    view = finder.findRequiredView(source, 2131558596, "field 'lv_addgroup'");
    target.lv_addgroup = finder.castView(view, 2131558596, "field 'lv_addgroup'");
    view = finder.findRequiredView(source, 2131558590, "field 'tb_addgroup'");
    target.tb_addgroup = finder.castView(view, 2131558590, "field 'tb_addgroup'");
    view = finder.findRequiredView(source, 2131558594, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558595, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.et_name = null;
    target.lv_addgroup = null;
    target.tb_addgroup = null;
  }
}
