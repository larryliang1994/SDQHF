// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_AddMemo$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_AddMemo> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493010, "field 'edt_content' and method 'onClick'");
    target.edt_content = finder.castView(view, 2131493010, "field 'edt_content'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493007, "field 'edt_address'");
    target.edt_address = finder.castView(view, 2131493007, "field 'edt_address'");
    view = finder.findRequiredView(source, 2131493008, "field 'cb_needNotify' and method 'onClick'");
    target.cb_needNotify = finder.castView(view, 2131493008, "field 'cb_needNotify'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493005, "field 'sv'");
    target.sv = finder.castView(view, 2131493005, "field 'sv'");
    view = finder.findRequiredView(source, 2131493004, "field 'tb_addmemo'");
    target.tb_addmemo = finder.castView(view, 2131493004, "field 'tb_addmemo'");
    view = finder.findRequiredView(source, 2131493006, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493009, "method 'onClick'");
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
    target.edt_content = null;
    target.edt_address = null;
    target.cb_needNotify = null;
    target.sv = null;
    target.tb_addmemo = null;
  }
}
