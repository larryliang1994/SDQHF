// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_CheckMsg$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_CheckMsg> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493022, "field 'tv_content'");
    target.tv_content = finder.castView(view, 2131493022, "field 'tv_content'");
    view = finder.findRequiredView(source, 2131493021, "field 'tv_sendtime'");
    target.tv_sendtime = finder.castView(view, 2131493021, "field 'tv_sendtime'");
    view = finder.findRequiredView(source, 2131493020, "field 'tb_checkmsg'");
    target.tb_checkmsg = finder.castView(view, 2131493020, "field 'tb_checkmsg'");
  }

  @Override public void unbind(T target) {
    target.tv_content = null;
    target.tv_sendtime = null;
    target.tb_checkmsg = null;
  }
}
