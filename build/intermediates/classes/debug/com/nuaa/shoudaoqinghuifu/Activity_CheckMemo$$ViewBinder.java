// Generated code from Butter Knife. Do not modify!
package com.nuaa.shoudaoqinghuifu;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Activity_CheckMemo$$ViewBinder<T extends com.nuaa.shoudaoqinghuifu.Activity_CheckMemo> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493011, "field 'tv_time_happen'");
    target.tv_time_happen = finder.castView(view, 2131493011, "field 'tv_time_happen'");
    view = finder.findRequiredView(source, 2131493012, "field 'tv_content'");
    target.tv_content = finder.castView(view, 2131493012, "field 'tv_content'");
    view = finder.findRequiredView(source, 2131493015, "field 'tv_address'");
    target.tv_address = finder.castView(view, 2131493015, "field 'tv_address'");
    view = finder.findRequiredView(source, 2131493013, "field 'tv_time_memo_title'");
    target.tv_time_memo_title = finder.castView(view, 2131493013, "field 'tv_time_memo_title'");
    view = finder.findRequiredView(source, 2131493014, "field 'tv_time_memo'");
    target.tv_time_memo = finder.castView(view, 2131493014, "field 'tv_time_memo'");
    view = finder.findRequiredView(source, 2131493009, "field 'tb_checkmemo'");
    target.tb_checkmemo = finder.castView(view, 2131493009, "field 'tb_checkmemo'");
    view = finder.findRequiredView(source, 2131493010, "field 'sv'");
    target.sv = finder.castView(view, 2131493010, "field 'sv'");
  }

  @Override public void unbind(T target) {
    target.tv_time_happen = null;
    target.tv_content = null;
    target.tv_address = null;
    target.tv_time_memo_title = null;
    target.tv_time_memo = null;
    target.tb_checkmemo = null;
    target.sv = null;
  }
}
