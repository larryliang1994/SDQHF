// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Feedback$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Feedback> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493033, "field 'edt_content'");
    target.edt_content = finder.castView(view, 2131493033, "field 'edt_content'");
    view = finder.findRequiredView(source, 2131493034, "field 'edt_contact'");
    target.edt_contact = finder.castView(view, 2131493034, "field 'edt_contact'");
    view = finder.findRequiredView(source, 2131493032, "field 'tb_feedback'");
    target.tb_feedback = finder.castView(view, 2131493032, "field 'tb_feedback'");
    view = finder.findRequiredView(source, 2131493035, "method 'onClick'");
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
    target.edt_contact = null;
    target.tb_feedback = null;
  }
}
