# 简介

本文档说明Android 识别sdk demo项目的代码。即在识别sdk（jar及so文件）上封装的一系列的类及方法，明确调用流程，减少开发者的使用成本。

读此文档前，请先查看sdk文档，熟悉识别sdk的调用方式及参数。https://ai.baidu.com/docs#/ASR-API/top

如果你打算直接调用sdk，也可以参考下demo中的实现。



# Modules

demo 一共分为4个模块：

1. app 主模块， 使用core模块及ui模块。主要定义了Activity。
2. core 模块， sdk库的简单封装处理
3. uiasr模块，demo ui相关的代码，与core模块独立
4. uidialog， 对应于demo中对话框的ui相关代码。



本文档着重讲解core模块。



# 识别

识别在demo中由 MyRecognizer类控制。在ActivityAbstractRecog可以找到相关的调用方法。



输入语音 -  来自麦克风或者由IN_FILE参数 指定

输入参数 - 由用户指定，通过START事件传递后启动识别

输出回调 -  通过用户定义的回调类



## MyRecognizer

```java
// 初始化，一般在onCreate或者onResume中

new MyRecognizer(context,recogListener);

// 第二个参数recogListener类作为sdk的输出回调，实现IRecogListener或者EventListener接口，在下一节解释。

// 只允许同时new一个对象，如果想new一个新的，必须调用之前对象的release（）方法释放
// isInited 判断是否存在未被释放的MyRecognizer类

```



### 开始识别

```java
start(Map<String, Object> params)；
// params为具体的参数Map，之后会转换为json，传递到sdk中
```



### 停止识别

手动停止识别，告知sdk音频输入完毕

```java
stop() 
```



### 取消识别

放弃本次识别

```java
cancel() 
```

### 释放资源

```
release() 
//释放后才能new 一个新的MyRecognizer
```



SDK中没有这一步，主要调用了“取消识别“ 和释放离线资源

### 重新设定回调listener

```java
setEventListener(recogListener)
```



### 加载离线命令词资源

```java
loadOfflineEngine(Map<String, Object> params) 
```

在start前调用，收到加载成功的ASR_KWS_LOAD_ENGINE回调后才可以调用start方法开始识别。建议new之后立即调用loadOfflineEngine。

对于ASR_KWS_LOAD_ENGINE事件，一般很快就有回调，因此demo的MyRecognizer类缺少这个同步判断。**即如果需要loadOfflineEngine之后立即调用start方法，需要等待ASR_KWS_LOAD_ENGINE事件**。


## 回调 EventListener

SDK只接受EventListener作为回调接口，但是EventListener没有做过多的结果解析。IRecogListener是demo中使用的识别回调接口，做了大量的结果解析，解析逻辑可以参见RecogEventAdapter



IRecogListener 接口有如下实现

- StatusRecogListener 加上识别状态
- MessageStatusRecogListener 在StatusRecogListener基础上加上handler及具体的回调内容
- ChainRecogListener 可以设置多个 IRecogListener

# IN_FILE 外部音频

IN_FILE 参数允许输入pcm文件和InputStream，来代替麦克风的输入的语音

demo中有如下3个InputStream的相关类

### InFileStream

IN_FILE参数要求输入字符串，用反射的方式获取具体的InputStream类。



```
#com.baidu.aip.asrwakeup3.core.inputstream.create16kStream() 表示调用如下静态方法获取InputStream作为输入

InputStream yourOwnInput = com.baidu.aip.asrwakeup3.core.inputstream.create16kStream()；
```



InFileStream 默认使用 assets目录下的outfile.pcm， 包装成FileAudioInputStream给传递给sdk。



```java
/**
     * 默认从createFileStream中读取InputStream
     * @return
     */
    public static InputStream create16kStream() {
        if (is != null) { // 默认为null，setInputStream调用后走这个逻辑
            return new FileAudioInputStream(is);
        } else if (filename != null) { //  默认为null， setFileName调用后走这个逻辑
            try {
                return new FileAudioInputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            // 默认从createFileStream中读取
            return new FileAudioInputStream(createFileStream());
        }
        return null;
    }
```



### FileAudioInputStream

SDK中对IN_FILE参数输入的InputStream或者音频文件有限制，即不能传递“过快”，比如pcm文件过大时，会报错。

FileAudioInputStream这个类对传输速度进行了限制，即每隔20ms读取数据，如果发现传输“过快”，会强制sleep

```java
int count = bytePerMs * 20; // 20ms 音频数据
if (byteCount < count) {
    count = byteCount;
}
if (nextSleepTime > 0) {
    try {
        long sleepMs = nextSleepTime - System.currentTimeMillis();
        if (sleepMs > 0) {
            Log.i(TAG, "will sleep " + sleepMs);
```



### MyMicrophoneInputStream

使用麦克风的音频数据作为InputStream，通过IN_FILE参数传递给SDK。 如果对SDK的录音不满意，可以修改使用这个类。


# 唤醒

唤醒在demo中由 MyWakeup类控制。在ActivityWakeUpRecog可以找到相关的调用方法。


输入语音 -  来自麦克风或者由IN_FILE参数 指定

输入参数 - 由用户指定，通过START事件传递后启动识别

输出回调 -  通过用户定义的回调类


## MyWakeup

```java
// 初始化，一般在onCreate或者onResume中

new MyWakeup(context,eventListener);

// 第二个参数eventListener类作为sdk的输出回调，实现EventListener或者IWakeupListener接口，在下一节解释。

// 只允许同时new一个对象，如果想new一个新的，必须调用之前对象的release（）方法释放
// isInited 判断是否存在未被释放的MyWakeup类

```

### 开始唤醒
```java
 start(Map<String, Object> params) 
 // params为具体的参数Map，之后会转换为json，传递到sdk中
```



### 停止唤醒

```java
stop()	
```



### 释放资源

```
release() 
//释放后才能new 一个新的MyWakeup
```



SDK中没有这一步



### 重新设定回调listener

```java
setEventListener(recogListener)
```



## 回调 EventListener

SDK只接受EventListener作为回调接口，但是EventListener没有做过多的结果解析。IWakeupListener是demo中使用的识别回调接口，做了大量的结果解析，解析逻辑可以参见WakeupEventAdapter



IWakeupListener的实现类

- SimpleWakeupListener 打印了回调信息
- RecogWakeupListener 在SimpleWakeupListener的基础上 加上handler