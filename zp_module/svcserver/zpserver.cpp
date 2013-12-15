#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>
#include <grp.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include <utils/Log.h>
#include "../service/ZPService.h"

using namespace android;


int main(int arg, char** argv)
{
    printf("server - ain() begin\n");
    sp<ProcessState> proc(ProcessState::self());
    sp<IServiceManager> sm = defaultServiceManager();
    //printf("ServiceManager: %p\n", sm.get());
    printf("server - erviceManager: %p\n", sm.get());
    int ret = ZPService::Instance();
    printf("server - ZPService::Instance return %d\n", ret);
    ProcessState::self()->startThreadPool();
	IPCThreadState::self()->joinThreadPool();
}
