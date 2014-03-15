package com.example.aidl;
import com.example.aidl.Book;
import com.example.aidl.ICallback;
interface IAIDLServerService {
      
    String sayHello();

    Book getBook();
    
    void registerCallback(ICallback cb);
    void unregisterCallback(ICallback cb);
}