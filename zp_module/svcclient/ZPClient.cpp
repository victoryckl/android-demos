#include <stdio.h>
#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include "ZPClient.h"


namespace android
{
    sp<IBinder> binder;

    int ZPClient::setN(int n)
    {
        getZPService();
        Parcel data, reply;
        data.writeInt32(getpid());
        data.writeInt32(n);

        printf("client - binder->transact()\n");
        binder->transact(0, data, &reply);
        int r = reply.readInt32();
        return r;
    }

    void ZPClient::getZPService()
    {
        sp<IServiceManager> sm = defaultServiceManager();
		printf("before getService()\n");
        binder = sm->getService(String16("zp.svc"));
        printf("client - getService: %p\n", sm.get());
        if(binder == 0)
        {
            printf("ZPService not published, waiting...\n");
            return;
        }
    }

}
