// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_About$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_About> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558584, "field 'tb_about'");
    target.tb_about = finder.castView(view, 2131558584, "field 'tb_about'");
    view = finder.findRequiredView(source, 2131558585, "field 'tv_version'");
    target.tv_version = finder.castView(view, 2131558585, "field 'tv_version'");
  }

  @Override public void unbind(T target) {
    target.tb_about = null;
    target.tv_version = null;
  }
}
