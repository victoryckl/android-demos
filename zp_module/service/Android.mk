LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)
LOCAL_SRC_FILES:=ZPService.cpp
LOCAL_SHARED_LIBRARIES:=libutils libbinder
LOCAL_MODULE_TAGS:=optional
LOCAL_MODULE:=libZPService
LOCAL_PRELINK_MODULE:=false
include $(BUILD_SHARED_LIBRARY)
