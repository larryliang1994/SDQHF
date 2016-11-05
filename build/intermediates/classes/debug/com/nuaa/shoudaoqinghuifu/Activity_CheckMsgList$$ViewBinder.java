// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_CheckMsgList$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_CheckMsgList> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558623, "field 'lv_checkList'");
    target.lv_checkList = finder.castView(view, 2131558623, "field 'lv_checkList'");
    view = finder.findRequiredView(source, 2131558622, "field 'tb_checkList'");
    target.tb_checkList = finder.castView(view, 2131558622, "field 'tb_checkList'");
  }

  @Override public void unbind(T target) {
    target.lv_checkList = null;
    target.tb_checkList = null;
  }
}
