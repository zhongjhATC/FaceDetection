package com.baidu.idl.main.facesdk.registerlibrary.user.manager;


import com.baidu.idl.main.facesdk.registerlibrary.user.api.FaceApi;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.User;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.LogUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用户管理
 * Created by v_liujialu01 on 2018/12/14.
 */

public class UserInfoManager {
    private static final String TAG = UserInfoManager.class.getSimpleName();
    private ExecutorService mExecutorService = null;

    // 私有构造
    private UserInfoManager() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
    }

    private static class HolderClass {
        private static final UserInfoManager instance = new UserInfoManager();
    }

    public static UserInfoManager getInstance() {
        return HolderClass.instance;
    }

    /**
     * 释放
     */
    public void release() {
        LogUtils.i(TAG, "release");
    }

    /**
     * 删除用户列表信息
     */
    public void deleteUserListInfo(final List<User> list, final UserInfoListener listener, final int selectCount) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                if (listener == null) {
                    return;
                }

                if (list == null) {
                    listener.userListDeleteFailure("参数异常");
                    return;
                }

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
                        FaceApi.getInstance().userDelete(list.get(i).getUserId());
                    }
                }
                listener.userListDeleteSuccess();
            }
        });
    }

    /**
     * 获取用户列表信息
     */
    public void getUserListInfo(final String userName, final UserInfoListener listener) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                if (listener == null) {
                    return;
                }

                // 如果关键字为null，则全局查找
                if (userName == null) {
                    listener.userListQuerySuccess(null, FaceApi.getInstance().getAllUserList());
                } else {
                    listener.userListQuerySuccess(userName,
                            FaceApi.getInstance().getUserListByUserNameVag(userName));
                }
            }
        });
    }

    public static class UserInfoListener {

        public void userListQuerySuccess(String userName, List<User> listUserInfo) {
            // 用户列表查询成功
        }

        public void userListQueryFailure(String message) {
            // 用户列表查询失败
        }

        public void userListDeleteSuccess() {
            // 用户列表删除成功
        }

        public void userListDeleteFailure(String message) {
            // 用户列表删除失败
        }
    }
}
