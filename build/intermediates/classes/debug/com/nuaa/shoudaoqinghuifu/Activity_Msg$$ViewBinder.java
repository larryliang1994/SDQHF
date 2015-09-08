// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_Msg$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_Msg> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558665, "field 'tv_empty'");
    target.tv_empty = finder.castView(view, 2131558665, "field 'tv_empty'");
    view = finder.findRequiredView(source, 2131558664, "field 'lv_msg'");
    target.lv_msg = finder.castView(view, 2131558664, "field 'lv_msg'");
    view = finder.findRequiredView(source, 2131558662, "field 'tb_msg'");
    target.tb_msg = finder.castView(view, 2131558662, "field 'tb_msg'");
    view = finder.findRequiredView(source, 2131558666, "field 'nv_msg'");
    target.nv_msg = finder.castView(view, 2131558666, "field 'nv_msg'");
    view = finder.findRequiredView(source, 2131558661, "field 'dw_msg'");
    target.dw_msg = finder.castView(view, 2131558661, "field 'dw_msg'");
  }

  @Override public void unbind(T target) {
    target.tv_empty = null;
    target.lv_msg = null;
    target.tb_msg = null;
    target.nv_msg = null;
    target.dw_msg = null;
  }
}
