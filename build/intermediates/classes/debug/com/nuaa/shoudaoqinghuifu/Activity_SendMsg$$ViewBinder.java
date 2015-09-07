// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_SendMsg$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_SendMsg> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558676, "field 'fab_send'");
    target.fab_send = finder.castView(view, 2131558676, "field 'fab_send'");
    view = finder.findRequiredView(source, 2131558672, "field 'et_names' and method 'onClick'");
    target.et_names = finder.castView(view, 2131558672, "field 'et_names'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558678, "field 'et_content'");
    target.et_content = finder.castView(view, 2131558678, "field 'et_content'");
    view = finder.findRequiredView(source, 2131558666, "field 'layout_sendmsg'");
    target.layout_sendmsg = finder.castView(view, 2131558666, "field 'layout_sendmsg'");
    view = finder.findRequiredView(source, 2131558668, "field 'tb_sendmsg'");
    target.tb_sendmsg = finder.castView(view, 2131558668, "field 'tb_sendmsg'");
    view = finder.findRequiredView(source, 2131558673, "field 'sv_sendmsg'");
    target.sv_sendmsg = finder.castView(view, 2131558673, "field 'sv_sendmsg'");
    view = finder.findRequiredView(source, 2131558677, "field 'iv_pen'");
    target.iv_pen = finder.castView(view, 2131558677, "field 'iv_pen'");
    view = finder.findRequiredView(source, 2131558680, "field 'ibtn_temp'");
    target.ibtn_temp = finder.castView(view, 2131558680, "field 'ibtn_temp'");
    view = finder.findRequiredView(source, 2131558683, "field 'ibtn_settime'");
    target.ibtn_settime = finder.castView(view, 2131558683, "field 'ibtn_settime'");
    view = finder.findRequiredView(source, 2131558671, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558684, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131558681, "method 'onClick'");
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
    target.fab_send = null;
    target.et_names = null;
    target.et_content = null;
    target.layout_sendmsg = null;
    target.tb_sendmsg = null;
    target.sv_sendmsg = null;
    target.iv_pen = null;
    target.ibtn_temp = null;
    target.ibtn_settime = null;
  }
}
