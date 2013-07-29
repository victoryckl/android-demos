package com.chapter8.aidl;
import com.chapter8.aidl.Book;
import com.chapter8.aidl.ICallback;
interface IAIDLServerService {
      
    String sayHello();

    Book getBook();
    
    void registerCallback(ICallback cb);
    void unregisterCallback(ICallback cb);
}