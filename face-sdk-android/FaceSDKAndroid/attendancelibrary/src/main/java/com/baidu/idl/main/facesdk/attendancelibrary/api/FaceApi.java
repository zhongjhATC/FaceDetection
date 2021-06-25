/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.idl.main.facesdk.attendancelibrary.api;

import android.graphics.Bitmap;
import android.text.TextUtils;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.attendancelibrary.db.DBManager;
import com.baidu.idl.main.facesdk.attendancelibrary.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.attendancelibrary.model.Group;
import com.baidu.idl.main.facesdk.attendancelibrary.model.ImportFeatureResult;
import com.baidu.idl.main.facesdk.attendancelibrary.model.User;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.Feature;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaceApi {
    private static FaceApi instance;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;

    private int mUserNum;
    private boolean isinitSuccess = false;


    private FaceApi() {

    }

    public static synchronized FaceApi getInstance() {
        if (instance == null) {
            instance = new FaceApi();
        }
        return instance;
    }

    /**
     * 添加用户组
     */
    public boolean groupAdd(Group group) {
        if (group == null || TextUtils.isEmpty(group.getGroupId())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_-]{1,}$");
        Matcher matcher = pattern.matcher(group.getGroupId());
        if (!matcher.matches()) {
            return false;
        }
        boolean ret = DBManager.getInstance().addGroup(group);

        return ret;
    }

    /**
     * 查询用户组（默认最多取1000个组）
     */
    public List<Group> getGroupList(int start, int length) {
        if (start < 0 || length < 0) {
            return null;
        }
        if (length > 1000) {
            length = 1000;
        }
        List<Group> groupList = DBManager.getInstance().queryGroups(start, length);
        return groupList;
    }

    /**
     * 根据groupId查询用户组
     */
    public List<Group> getGroupListByGroupId(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }
        return DBManager.getInstance().queryGroupsByGroupId(groupId);
    }

    /**
     * 根据groupId删除用户组
     */
    public boolean groupDelete(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return false;
        }
        boolean ret = DBManager.getInstance().deleteGroup(groupId);
        return ret;
    }

    /**
     * 添加用户
     */
    public boolean userAdd(User user) {
        if (user == null || TextUtils.isEmpty(user.getGroupId())) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z_-]{1,}$");
        Matcher matcher = pattern.matcher(user.getUserId());
        if (!matcher.matches()) {
            return false;
        }
        boolean ret = DBManager.getInstance().addUser(user);
        return ret;
    }

    /**
     * 查找所有用户
     * @return
     */
    public List<User> getAllUserList() {
        return DBManager.getInstance().queryAllUsers();
    }

    /**
     * 根据userName查找用户（精确查找）
     */
    public List<User> getUserListByUserName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return null;
        }
        return DBManager.getInstance().queryUserByUserNameAccu(userName);
    }

    /**
     * 根据userName查找用户（模糊查找）
     */
    public List<User> getUserListByUserNameVag(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return null;
        }
        return DBManager.getInstance().queryUserByUserNameVag(userName);
    }

    /**
     * 根据_id查找用户
     */
    public User getUserListById(int _id) {
        if (_id < 0) {
            return null;
        }
        List<User> userList = DBManager.getInstance().queryUserById(_id);
        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    /**
     * 更新用户
     */
    public boolean userUpdate(User user) {
        if (user == null) {
            return false;
        }

        boolean ret = DBManager.getInstance().updateUser(user);
        return ret;
    }

    /**
     * 更新用户
     */
    public boolean userUpdate(String userName, String imageName, byte[] feature) {
        if (userName == null || imageName == null || feature == null) {
            return false;
        }

        boolean ret = DBManager.getInstance().updateUser(userName, imageName, feature);
        return ret;
    }

    /**
     * 删除用户
     */
    public boolean userDelete(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        boolean ret = DBManager.getInstance().deleteUser(userId);
        return ret;
    }

    /**
     * 远程删除用户
     */
    public boolean userDeleteByName(String userName) {
        if (TextUtils.isEmpty(userName)) {
            return false;
        }

        boolean ret = DBManager.getInstance().userDeleteByName(userName);
        return ret;
    }

    /**
     * 是否是有效姓名
     *
     * @param username 用户名
     * @return 有效或无效信息
     */
    public String isValidName(String username) {
        if (username == null || "".equals(username.trim())) {
            return "姓名为空";
        }

        // 姓名过长
        if (username.length() > 10) {
            return "姓名过长";
        }

        String regex0 = "[ `~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）—"
                + "—+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p0 = Pattern.compile(regex0);
        Matcher m0 = p0.matcher(username);
        if (m0.find()) {
            return "姓名中含有特殊符号";
        }

        // 含有特殊符号
        String regex = "^[0-9a-zA-Z_\\u3E00-\\u9FA5]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        if (!m.find()) {
            return "姓名中含有特殊符号";
        }
        return "0";
    }

    /**
     * 提取特征值
     */
    public ImportFeatureResult getFeature(Bitmap bitmap, byte[] feature, BDFaceSDKCommon.FeatureType featureType) {
        if (bitmap == null) {
            return new ImportFeatureResult(-1, null);
        }

        BDFaceImageInstance imageInstance = new BDFaceImageInstance(bitmap);
        // 最大检测人脸，获取人脸信息
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, imageInstance);
        if (faceInfos == null || faceInfos.length == 0) {
            imageInstance.destory();
            return new ImportFeatureResult(-2, null);
        }
        FaceInfo faceInfo = faceInfos[0];
        // 人脸识别，提取人脸特征值
        float ret = FaceSDKManager.getInstance().getFaceFeature().feature(
                featureType, imageInstance,
                faceInfo.landmarks, feature);
        imageInstance.destory();
        return null;
    }


    public boolean registerUserIntoDBmanager(String groupName, String userName, String picName,
                                             String userInfo, byte[] faceFeature) {
        boolean isSuccess = false;

        User user = new User();
        user.setGroupId(DBManager.GROUP_ID);
        // 用户id（由数字、字母、下划线组成），长度限制128B
        // uid为用户的id,百度对uid不做限制和处理，应该与您的帐号系统中的用户id对应。
        final String uid = UUID.randomUUID().toString();
        user.setUserId(uid);
        user.setUserName(userName);
        user.setFeature(faceFeature);
        user.setImageName(picName);
        if (userInfo != null) {
            user.setUserInfo(userInfo);
        }
        // 添加用户信息到数据库
        return FaceApi.getInstance().userAdd(user);
    }

    /**
     * 获取底库数量
     *
     * @return
     */
    public int getmUserNum() {
        return mUserNum;
    }

    public boolean isinitSuccess() {
        return isinitSuccess;
    }

    /**
     * 数据库发现变化时候，重新把数据库中的人脸信息添加到内存中，id+feature
     */
    public void initDatabases(final boolean isFeaturePush) {

        if (future != null && !future.isDone()) {
            future.cancel(true);
        }

        isinitSuccess = false;
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                ArrayList<Feature> features = new ArrayList<>();
                List<User> listUser = FaceApi.getInstance().getAllUserList();
                for (int j = 0; j < listUser.size(); j++) {
                    Feature feature = new Feature();
                    feature.setId(listUser.get(j).getId());
                    feature.setFeature(listUser.get(j).getFeature());
                    features.add(feature);
                }
                if (isFeaturePush) {
                    FaceSDKManager.getInstance().getFaceFeature().featurePush(features);
                }
                mUserNum = features.size();
                isinitSuccess = true;
            }
        });
    }

    // 删除识别记录
    public boolean deleteRecords(String userName) {
        boolean ret = false;
        if (TextUtils.isEmpty(userName)) {
            return ret;
        }
        ret = DBManager.getInstance().deleteRecords(userName);
        return ret;
    }

    // 删除识别记录
    public boolean deleteRecords(String startTime, String endTime) {
        boolean ret = false;
        if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
            return ret;
        }
        ret = DBManager.getInstance().deleteRecords(startTime, endTime);
        return ret;
    }

    // 清除识别记录
    public int cleanRecords() {
        boolean ret = false;
        int num = DBManager.getInstance().cleanRecords();
        return num;
    }


}
