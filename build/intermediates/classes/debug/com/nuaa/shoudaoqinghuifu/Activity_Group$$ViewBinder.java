// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Group$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Group> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493037, "field 'tv_empty'");
    target.tv_empty = finder.castView(view, 2131493037, "field 'tv_empty'");
    view = finder.findRequiredView(source, 2131493036, "field 'elv_group'");
    target.elv_group = finder.castView(view, 2131493036, "field 'elv_group'");
    view = finder.findRequiredView(source, 2131493034, "field 'tb_group'");
    target.tb_group = finder.castView(view, 2131493034, "field 'tb_group'");
    view = finder.findRequiredView(source, 2131493038, "field 'nv_group'");
    target.nv_group = finder.castView(view, 2131493038, "field 'nv_group'");
    view = finder.findRequiredView(source, 2131493033, "field 'dw_group'");
    target.dw_group = finder.castView(view, 2131493033, "field 'dw_group'");
  }

  @Override public void unbind(T target) {
    target.tv_empty = null;
    target.elv_group = null;
    target.tb_group = null;
    target.nv_group = null;
    target.dw_group = null;
  }
}
