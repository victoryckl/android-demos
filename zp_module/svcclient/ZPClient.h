#ifndef ANDROID_ZPCLIENT_H  
#define ANDROID_ZPCLIENT_H  
  
namespace android  
{  
    class ZPClient  
    {  
    public:  
        int setN(int n);  
    private:  
        static void getZPService();  
    };  
}  
  
#endif  
