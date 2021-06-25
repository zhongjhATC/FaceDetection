#!/bin/sh
##############################################################################################
# 导出sdk功能
# 作者：yangrui09
#
##############################################################################################

#定义当前工作空间
BDFACE_WORKSPACE_DIR=`pwd`
BDFACE_WORKSPACE="${BDFACE_WORKSPACE_DIR}/FaceSDKAndroid"
BDFACE_WORKSPACE_OUTPUT="${BDFACE_WORKSPACE_DIR}/output"
workpath=$(cd `dirname $0`; pwd)
outputpath=$workpath"/output"
outputsdklibspath=$outputpath"/libs"
outputsdkassetpath=$outputpath"/assets"
outputsdkdemopath=$outputpath"/demo"
outputsdkapkpath=$outputpath"/apk"


function BDFaceSubmodule
{
    # submodule update
    git submodule update --init --recursive
}

function BDFaceConfig
{
    # submodule update
    BDFaceSubmodule

    # config agile compile
    ANDROID_SDK_HOME=/home/scmtools/buildkit/android-sdk
    if [ -d "$ANDROID_SDK_HOME" ]; then
    echo "ANDROID_SDK_HOME exist,so current is aigile"
    JAVA_HOME=/home/scmtools/buildkit/jdk-1.8u92
    ANDROID_NDK_HOME=/home/scmtools/buildkit/android-ndk-r12b
    JAVA_GRADLE=/home/scmtools/buildkit/gradle/gradle-2.4/bin
    PATH=$JAVA_GRADLE:$ANDROID_SDK_HOME:$JAVA_HOME:$ANDROID_NDK_HOME:$PATH
    fi

    clear
    date
}

# clean old products
function BDFaceCleanOldProducts()
{
    echo 1.------- create dir -------

    rm -rf $outputpath
    mkdir $outputpath
    echo $outputpath
    rm -rf $outputsdklibspath
    mkdir $outputsdklibspath
    rm -rf $outputsdkassetpath
    mkdir $outputsdkassetpath
    rm -rf $outputsdkdemopath
    mkdir $outputsdkdemopath
    rm -rf $outputsdkapkpath
    mkdir $outputsdkapkpath
    # output最终输出产物路径
    rm -rf "${BDFACE_WORKSPACE_OUTPUT}"
    mkdir -p "${BDFACE_WORKSPACE_OUTPUT}"

    echo   ---------------------------------------------------------------
}

function BDFaceBuildProduts()
{
    echo 2 ------- begin build -------
    echo "${BDFACE_WORKSPACE}"
    cd "${BDFACE_WORKSPACE}"

    chmod +x $workpath"/gradlew"

    echo  -------  build clean-------
    ./gradlew clean
    echo  -------  build assembleRelease-------
    ./gradlew assembleRelease

    echo   ---------------------------------------------------------------
}

function BDFaceCopyProducts()
{
    echo 3 ------- copy assets -------
    sdkassetpath=$workpath"/facelibrary/src/main/assets/*"
    cp -rf $sdkassetpath  $outputsdkassetpath

    echo   ---------------------------------------------------------------
    echo 4.1 ------- copy FaceSDK lib -------
    facelibrarysopath=$workpath"/facelibrary/build/intermediates/merged_jni_libs/release/*"
    facelibraryjarspath=$workpath"/facelibrary/build/intermediates/packaged-classes/release/libs/*"
    # facelibraryndkbuildpath=$workpath"/facelibrary/build/intermediates/cmake/release/obj/*"
#    facelibraryclassspath=$workpath"/facelibrary/build/intermediates/packaged-classes/release/classes.jar"

    ls $workpath"/facelibrary/build/intermediates/bundles/library_and_local_jars_jni/release/armeabi-v7a/"
    cp -rf $facelibrarysopath $outputsdklibspath
    echo ====after copy==
    ls $outputsdklibspath"/armeabi-v7a/"

    cp -rf $facelibraryjarspath $outputsdklibspath
#    versionName=`cat facelibrary/build.gradle | grep versionName |awk  -F" " '{print $2}' | sed 's/\"//g'`
#    cp -f $facelibraryclassspath $outputsdklibspath"/FaceSDK-${versionName}.jar"
    cp -rf $facelibraryndkbuildpath $outputsdkndkbuildpath
    # 删除libs目录下x86的文件
    rm -r $outputsdklibspath"/x86/"

    echo   ---------------------------------------------------------------
    echo 4.2 ------- copy demo  -------
    demoapppath=$workpath"/app"
    buildgradlepath=$workpath"/build.gradle"
    gradlepath=$workpath"/gradle"
    gradlepropertiespath=$workpath"/gradle.properties"
    gradlewpath=$workpath"/gradlew"
    localpropertiespath=$workpath"/local.properties"
    settingsgradlepath=$workpath"/settings.gradle"
    cp -r -f $demoapppath $outputsdkdemopath
    cp $buildgradlepath $outputsdkdemopath
    cp -r $gradlepath $outputsdkdemopath
    cp $gradlepropertiespath $outputsdkdemopath
    cp $gradlewpath $outputsdkdemopath
    cp $localpropertiespath $outputsdkdemopath
    cp $settingsgradlepath $outputsdkdemopath

    echo   ---------------------------------------------------------------
    echo 4.3 copy sdklib to demo

    cp -r $outputsdklibspath  $outputsdkdemopath"/app/"

    echo
    echo 4.4 copy others
    democoverpath=$workpath"/tools/demo/"
    cp -f $democoverpath"settings.gradle" $outputsdkdemopath"/settings.gradle"
    cp -f $democoverpath"/app/build.gradle" $outputsdkdemopath"/app/build.gradle"

    echo   ---------------------------------------------------------------
}

function BDFaceRunDemo()
{
    echo 5.0 ------- create apk demo -------

    demoapkpath=$demoapppath"/build/outputs/apk/*"
    cp -rf $demoapkpath $outputsdkapkpath

    #删除demo中的build目录
    rm -r $outputsdkdemopath"/app/build/"
    rm -r $outputsdkdemopath"/build/"

    echo   ---------------------------------------------------------------
}

function BDFaceZipProducts()
{
    echo 6.0 ------- to zip -------
    cd $outputpath
    time="`date +%Y%m%d%H%M%S`"
    #SMT_${filename}_${channel}_${vn}_${vc}_${time}.apk
    zip -r FaceSDK_${time}_R.zip .

    # 清除多余文件
    rm -rf $outputsdklibspath
    rm -rf $outputsdkassetpath
    rm -rf $outputsdkdemopath
    #rm -rf $outputsdkapkpath

    # copy需要zip的产物到output，如果输出产物直接到output，请去掉这个cp
    faceOutputAll=$outputpath"/*"
    cp -rf $faceOutputAll "${BDFACE_WORKSPACE_OUTPUT}"
}

# start
function StartBDFaceBuild()
{
    # 0. agile config
    BDFaceConfig

    # 1. rm old products
    BDFaceCleanOldProducts

    # 2. build new products
    BDFaceBuildProduts

    # 3. copy products
    BDFaceCopyProducts

    # 4. demo
    BDFaceRunDemo

    # 5. zip products
    BDFaceZipProducts
}

echo "\033[01;36m===Begin==="

# 记录起始时间
StartDate=`date +%s`

# 启动
StartBDFaceBuild;

# 结束时间
EndDate=`date +%s`;

# 编译总时间
_USED_TIME=$[ EndDate - StartDate ]

echo "\033[01;36m *****build all times : ${_USED_TIME} s******"

echo "\033[01;36m===End==="

