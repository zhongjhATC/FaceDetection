# 简介
蓝牙耳机需要在识别前开启SCO。一部分手机不支持

## 代码

见ActivityAbstractRecog 或全文搜索 `BluetoothUtil`

```java
// 识别前运行
 BluetoothUtil.start(this,BluetoothUtil.FULL_MODE); 

// 识别后不需要蓝牙耳机后注销
 BluetoothUtil.destory(this);
 
```

## 模式

- FULL_MODE: 较完整的开启代码
- SIMPLE_MODE: 简单的代码，较多SDK都是加这几行作为蓝牙耳机功能

参考代码：https://github.com/BelledonneCommunications/linphone-android

## 部分机型的测试结果

