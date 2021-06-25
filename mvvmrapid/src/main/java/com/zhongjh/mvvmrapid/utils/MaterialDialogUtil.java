//package com.zhongjh.mvvmrapid.utils;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.text.InputType;
//import android.text.TextUtils;
//
//import com.afollestad.materialdialogs.DialogAction;
//import com.afollestad.materialdialogs.GravityEnum;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.zhongjh.mvvmrapid.R;
//
//import java.util.List;
//
///**
// * Created by zhongjh on 2021/3/25.
// * 弹窗的方法
// */
//public class MaterialDialogUtil {
//
//    public void showThemed(Context context, String
//            title, String content) {
//        new MaterialDialog.Builder(context)
//                .title(title)
//                .content(content)
//                .positiveText("agree")
//                .negativeText("disagree")
//                .positiveColorRes(R.color.white)
//                .negativeColorRes(R.color.white)
//                .titleGravity(GravityEnum.CENTER)
//                .titleColorRes(R.color.white)
//                .contentColorRes(android.R.color.white)
//                .backgroundColorRes(R.color.material_blue_grey_800)
//                .dividerColorRes(R.color.white)
//                .btnSelector(R.drawable.md_selector, DialogAction.POSITIVE)
//                .positiveColor(Color.WHITE)
//                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
//                .theme(Theme.DARK)
//                .autoDismiss(true)
//                //点击是否关闭对话框
//                .showListener(dialog -> {
//                    //dialog 出现
//                })
//                .cancelListener(dialog -> {
//                    //dialog 消失（返回键）
//                })
//                .dismissListener(dialog -> {
//                    //dialog 消失
//                })
//                .show();
//    }
//
//    /***
//     * 获取一个耗时等待对话框
//     *
//     * @param title 标题
//     * @param horizontal 是否水平的进度条
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showIndeterminateProgressDialog(Context context, String title, boolean horizontal) {
//        return new MaterialDialog.Builder(context)
//                .title(title)
//                .progress(true, 0)
//                .progressIndeterminateStyle(horizontal)
//                .canceledOnTouchOutside(false)
//                .backgroundColorRes(R.color.white)
//                .keyListener((dialog, keyCode, event) -> {
////                    if (event.getAction() == KeyEvent.ACTION_DOWN) {//如果是按下，则响应，否则，一次按下会响应两次
////                        if (keyCode == KeyEvent.KEYCODE_BACK) {
////                            //activity.onBackPressed();
////
////                        }
////                    }
//                    // false允许按返回键取消对话框，true除了调用取消，其他情况下不会取消
//                    return false;
//                });
//    }
//
//
//    /***
//     * 获取基本提示框 只有确定
//     *
//     * @param title 标题
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicDialog(final Context context, String title) {
//        return new MaterialDialog.Builder(context)
//                .title(title)
//                .positiveText("确定");
//    }
//
//    /***
//     * 显示一个基础的对话框  只有内容没有标题
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicDialogNoTitle(final Context context, String content) {
//        return new MaterialDialog.Builder(context)
//                .content(content)
//                .positiveText("确定");
//    }
//
//
//    /***
//     * 显示一个基础的对话框  带标题 带内容
//     * 没有取消按钮
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicDialogNoCancel(final Context context, String
//            title, String content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .content(content)
//                .positiveText("确定");
//
//        return builder;
//    }
//
//    /***
//     * 显示一个基础的对话框  带标题 带内容
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicDialog(final Context context, String
//            title, String content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .content(content)
//                .positiveText("确定")
//                .negativeText("取消");
//
//        return builder;
//    }
//
//    /***
//     * 显示一个基础的对话框  带标题 带内容
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicDialogPositive(final Context context, String
//            title, String content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .content(content)
//                .positiveText("复制")
//                .negativeText("取消");
//
//        return builder;
//    }
//
//    /***
//     * 选择图片等Item的对话框  带标题
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder getSelectDialog(Context context, String title, String[] arrays) {
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .items(arrays)
//                .itemsColor(0XFF456ea6)
//                .negativeText("取消");
//        if (!TextUtils.isEmpty(title)) {
//            builder.title(title);
//        }
//        return builder;
//    }
//
//    /***
//     * 获取LIST对话框
//     *
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showBasicListDialog(final Context context, String title, List
//            content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .items(content)
//                .itemsCallback((dialog, itemView, position, text) -> {
//
//                })
//                .negativeText("取消")
////                .checkBoxPromptRes(R.string.app_name, false, null)
//                ;
//
//        return builder;
//    }
//
//    /***
//     * 获取单选LIST对话框
//     *
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showSingleListDialog(final Context context, String title, List
//            content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .items(content)
//                .itemsCallbackSingleChoice(1, (dialog, itemView, which, text) -> {
//
//
//                    return true; // allow selection
//                })
//                .positiveText("选择");
//
//        return builder;
//    }
//
//
//    /***
//     * 获取多选LIST对话框
//     *
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showMultiListDialog(final Context context, String title, List
//            content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .items(content)
//                .itemsCallbackMultiChoice(new Integer[]{1, 3}, (dialog, which, text) -> {
//
//
//                    return true; // allow selection
//                })
//                .onNeutral((dialog, which) -> dialog.clearSelectedIndices())
//                .alwaysCallMultiChoiceCallback()
//                .positiveText(R.string.md_choose_label)
//                .autoDismiss(false)
//                .neutralText("clear")
//                .itemsDisabledIndices(0, 1);
//
//        return builder;
//    }
//
//
//    /***
//     * 获取自定义对话框
//     *
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static void showCustomDialog(final Context context, String title, int
//            content) {
//
//        MaterialDialog dialog = new MaterialDialog.Builder(context)
//                .title(title)
//                .customView(content, true)
//                .positiveText("确定")
//                .negativeText(android.R.string.cancel)
//                .onPositive((dialog1, which) -> {
//
//                }).build();
//
////        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
////        //noinspection ConstantConditions
////        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
////        passwordInput.addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////            }
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                positiveAction.setEnabled(s.toString().trim().length() > 0);
////            }
////
////            @Override
////            public void afterTextChanged(Editable s) {
////            }
////        });
////
////        // Toggling the show password CheckBox will mask or unmask the password input EditText
////        CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
////        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
////                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
////            }
////        });
////
////        int widgetColor = ThemeSingleton.get().widgetColor;
////        MDTintHelper.setTint(checkbox,
////                widgetColor == 0 ? ContextCompat.getColor(this, R.color.accent) : widgetColor);
////
////        MDTintHelper.setTint(passwordInput,
////                widgetColor == 0 ? ContextCompat.getColor(this, R.color.accent) : widgetColor);
////
////        dialog.show();
////        positiveAction.setEnabled(false); // disabled by default
//
//    }
//
//
//    /***
//     * 获取输入对话框
//     *
//     * @param
//     * @return MaterialDialog.Builder
//     */
//    public static MaterialDialog.Builder showInputDialog(final Context context, String title, String
//            content) {
//
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
//                .title(title)
//                .content(content)
//                .inputType(InputType.TYPE_CLASS_TEXT |
//                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
//                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
//                .positiveText("确定")
//                .negativeText("取消")
//                .input("hint", "prefill", true, (dialog, input) -> {
//
//                });
//
//        return builder;
//    }
//
//}
