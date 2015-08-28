// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Welcome$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Welcome> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493090, "field 'layout'");
    target.layout = finder.castView(view, 2131493090, "field 'layout'");
  }

  @Override public void unbind(T target) {
    target.layout = null;
  }
}
