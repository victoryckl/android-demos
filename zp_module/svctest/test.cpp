#include <stdio.h>
#include "../svcclient/ZPClient.h"

using namespace android;

int main(int argc, char** argv)
{
    ZPClient client;
    int ret = client.setN(2012);
    printf("setN return: %d\n", ret);
    return 0;
}
