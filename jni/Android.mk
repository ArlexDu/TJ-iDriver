LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
LOCAL_PROGUARD_ENABLED := disabled
ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
include D:/Eclipse/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk
else  
include $(OPENCV_MK_PATH)  
endif

LOCAL_SRC_FILES  := tracker.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl
LOCAL_MODULE     := tracker
#LOCAL_JNI_SHARED_LIBRARIES := libtracker.so
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := BaiduMapSDK_v3_5_0_31
LOCAL_SRC_FILES := libBaiduMapSDK_v3_5_0_31.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := locSDK5
LOCAL_SRC_FILES := liblocSDK5.so
include $(PREBUILT_SHARED_LIBRARY)
