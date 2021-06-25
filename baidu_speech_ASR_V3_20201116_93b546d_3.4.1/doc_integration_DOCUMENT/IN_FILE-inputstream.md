## 简介

SDK默认输入是系统麦克风，如果想自行提供音频数据，可以使用IN_FILE参数。

本文描述如何使用IN_FILE参数输入外部音频，特别是使用InputStream作为音频流输入。


## 测试及参数说明

可以测试demo中的设置->外部音频参数

- /sdcard/test/test.pcm 绝对路径，pcm文件, 不长于3分钟
- res:///com/baidu/android/voicedemo/16k_test.pcm  pcm文件, 不长于3分钟
- #com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.create16kStream() , 从app/src/main/assets/outfile.pcm 作为文件流FileInputStream, 最终使用FileAudioInputStream输出
- #com.baidu.aip.asrwakeup3.core.inputstream.MyMicrophoneInputStream.getInstance(),  从麦克风读取实时音频数据

**以上音频文件，音频流都是 16000采样率，单声道，16bits小端序的pcm格式的音频数据。**



通常为了更灵活, 我们会使用InputStream作为二进制流输入。

Demo中的InputStream示例在core\src\main\java\com\baidu\aip\asrwakeup3\core\inputstream下

```
#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.create16kStream()
注意这个输入是字符串，必须以#开头，表示调用InFileStream.create16kStream()这个static方法，这个方法必须返回一个InputStream对象


res:///com/baidu/android/voicedemo/16k_test.pcm 表示resource目录，demo中在uiasr\src\main\resources\com\baidu\android\voicedemo这个位置
```
## InputStream 简介

https://developer.android.com/reference/java/io/InputStream

InputStream 是android/java的标准类， 如果需要详细的信息，可以自行“百度一下”。

InputStream代表数据二级制流，这里指音频流。 流的概念是数据是“慢慢”获取的：比如每20ms间隔获取20ms的录音数据， 流本身（及SDK）不知道也不关心之后20ms的音频数据。因为每隔20ms获取一次数据，从表现上来看，有几乎近似实时的效果。



### public int read(byte[] buffer, int offest, int length) throws IOException

SDK会一直循环调用你输入的InputStream里这个方法。



需要提供给SDK的音频数据为byte[] yourBytes，这个方法返回的值为int bytesSent。用以下代码说明逻辑：

```java
 @Override
public int read(byte[] buffer, int offest, int length) throws IOException {

    if (/* 流结束了 */) {
        return -1;
    } else if (/* 现在没数据了，但是流还没有结束 */) {
        // 考虑是否sleep一下，否则下次调用还是0的话，很耗CPU
        return 0;
    }
    int bytesSent = 0;

    if (yourBytes.length >= length) {
        // 如果你已有的音频数据大于SDK要求的length，则只需要开始的length部分

        /**
         * 这里是你自己获取length长度数据的逻辑
         */

        //  从yourBytes中复制从0开始，长度为length的数据到SDK的buffer中。表示传递给SDK数据
        System.arraycopy(yourBytes, 0, buffer, offest, length);
        bytesSent = length;
    } else {
        // 如果你已有的音频数据小于SDK要求的length， 即有多少复制多少

        /**
         * 这里是你自己获取length长度数据的逻辑
          */

        // 把yourBytes的数据到SDK的buffer中。覆盖从第offset位置开始，到offset+yourBytes.length位置结束。
        System.arraycopy(yourBytes, 0, buffer, offest, yourBytes.length);
        bytesSent = yourBytes.length;
    }

    // 保持流是实时传输的。如果需要可以sleep
    // 千万不能比如1小时的音频1s传递给SDK，1小时音频数据必须“缓缓地”1小时整传递SDK。
    return bytesSent;
}
```





## Demo中的 InputStream 示例及复用

- FileInputStream, 从文件或者已有完整数据的InputStream包装。适用于已经录制好的音频, 这个类里面有sleep代码。SDK要求的输入的音频是实时音频，如1小时的录音，SDK强制要求1小时传完，决不允许比如1秒传给SDK
- MyMicrophoneInputStream，使用实时的麦克风InputStream作为输入。 适用于实时获取音频的情况。 实时表示，比如20ms的录音是真实过了20ms获取的，因此不需要sleep
- InPipedStream， 使用文件模拟二进制输入。适用于原始音频是二进制byte[]的情况。



### 已经录制好的音频文件或者音频流，FileAudioInputStream

这里是指可以一下子把所有音频数据拿到的情况。

如前文描述，SDK要求的输入的音频是实时音频， 如果是已经录制好的音频数据，不能过快地传给SDK。 必须比如每传递20ms的音频数据，sleep 20ms， 确保1小时的音频，“缓缓地”正好在1小时传完。



调用方式：

```java
public FileAudioInputStream(String file) throws FileNotFoundException {
    in = new FileInputStream(file);
}

public FileAudioInputStream(InputStream in) {
    this.in = in;
}

```





### 实时的音频流， MyMicrophoneInputStream

如果已有InputStream音频流是实时的，并且不需要额外初始化逻辑的话。可以直接作为参数传递给SDK。



如果需要包装一下的话，可以参考MyMicrophoneInputStream。 

注意其中麦克风开启和关闭的时机。



### 实时的音频数据byte[], InPipedStream

如接口或者其它调用方式，可以获取byte[], 可以参考代码中使用PipedInputStream的输入方式。

InPipedStream类中使用InputStream作为byte[]的输入源。





### 自定义InputStream代码示例

当然你可以用一个现成的InputStream类，如果需要写自定义代码的话，可以参照下文

通常一个作为SDK输入的自定义InputStream有如下代码：



 ```java
public class MyInputStream extends InputStream {

    public MyInputStream{
    	// 这个是你自己new出来，比如在#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.create16kStream()
        
        // 一般获取一些资源
    }
    
    
    // SDK不会调用这个方法
    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }


    // SDK 只会调用这个方法
    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
    }

    // SDK不会调用这个方法, 但是这个方法很多时候是必须的，需要自行调用。可以参见本文末的说明
    @Override
    public void close(){
		// 释放构造函数里已经获取的资源
    }
}

 ```



可以大致认为SDK调用MyInputStream的代码是这样的。



```java

/**
* @param sdkInternalBuffer sdk 内部buffer
* @param offest            sdk 内部buffer需要写的offset
* @param length            sdk 需要获取的音频数据大小
*/
public void mock(byte[] sdkInternalBuffer, int offest, int length) throws IOException {
	// 实际情况是比如：反射获取“#com.baidu.aip.asrwakeup3.core.inputstream.InFileStream.create16kStream()” 字符串对应的类，调用create16kStream()方法，获得返回的InputStream
    InputStream is = new MyInputStream();
    int r = 0;
    while (r >= 0) {
        // 即r = -1 的时候退出
        r = is.read(sdkInternalBuffer, offest, length);
        // 如果音频流不是实时的，需要在read方法中sleep
    }
}
```



## 建议的集成步骤和调试方式

1. 测试官方demo
2. 选择一个demo中和你相近的InputStream，参照本文的，了解调用方式。
3. 使用assets/outfile.pcm作为测试输入，把FileAudioInputStream集成到你的代码里，调通代码。
4. 打印read的调用时间点和音频byte[]的md5，确认FileAudioInputStream和demo中的表现一致
5. 集成你自己的代码，确认read的调用时间点和音频byte[]的md5和FileAudioInputStream一致
6. 如果需要，在EXIT事件后，调用close



## 其它事项

长语音或者唤醒开启时，即使返回-1， SDK也不会结束识别，必须手动调用STOP输入事件，告知SDK音频输入结束。建议使用100ms的延时TimerTask任务。



SDK不会调用Close方法，demo为了代码的简洁也没有调用。

如果需要，建议在CALLBACK_EVENT_ASR_EXIT或唤醒的CALLBACK_EVENT
_WAKEUP_STOPED事件中调用。



