#include <stdio.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "ZPService.h"

namespace android
{
    //static struct sigaction oldact;
    static pthread_key_t sigbuskey;

    int ZPService::Instance()
    {
        printf("ZPService Instantiate\n");
        int ret = defaultServiceManager()->addService(
                String16("zp.svc"), new ZPService());
        printf("ZPService ret = %d\n", ret);
        return ret;
    }

    ZPService::ZPService()
    {
        printf("ZPService create\n");
        //m_NextConnId = 1;
        pthread_key_create(&sigbuskey,NULL);
    }

    ZPService::~ZPService()
    {
        pthread_key_delete(sigbuskey);
        printf("ZPService destory\n");
    }

    status_t ZPService::onTransact(uint32_t code,
                                   const Parcel& data,
                                   Parcel* reply,
                                   uint32_t flags)
    {
        switch(code)
        {
        case 0:
            {
                pid_t pid = data.readInt32();
                int num = data.readInt32();
                num += 1000;
                reply->writeInt32(num);
                return NO_ERROR;
            } break;
        default:
            return BBinder::onTransact(code, data, reply, flags);
        }
    }
}
