// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Contacts$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Contacts> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558632, "field 'lv_contacts'");
    target.lv_contacts = finder.castView(view, 2131558632, "field 'lv_contacts'");
    view = finder.findRequiredView(source, 2131558633, "field 'alpha'");
    target.alpha = finder.castView(view, 2131558633, "field 'alpha'");
    view = finder.findRequiredView(source, 2131558635, "field 'pb'");
    target.pb = finder.castView(view, 2131558635, "field 'pb'");
    view = finder.findRequiredView(source, 2131558631, "field 'tb_contacts'");
    target.tb_contacts = finder.castView(view, 2131558631, "field 'tb_contacts'");
  }

  @Override public void unbind(T target) {
    target.lv_contacts = null;
    target.alpha = null;
    target.pb = null;
    target.tb_contacts = null;
  }
}
