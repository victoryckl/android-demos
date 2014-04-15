package com.example.book;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.*;
import com.example.book.Util;

import java.net.URL;

public class MainActivity extends Activity {

    private TextView tx1;
    private Button btn;
    private Handler hd;
    private ProgressDialog mpd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StringBuffer str=new StringBuffer();
        str.append("1.按下扫描按钮启动第三方扫描软件，扫描并获取书籍的条形码；").append("\n");
        str.append("2.调用豆瓣API查询书籍介绍信息；").append("\n");
        str.append("3.显示在界面上").append("\n");
        tx1=(TextView)findViewById(R.id.main_textview01);
        tx1.setText(str.toString());
        btn=(Button)findViewById(R.id.main_button01);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator=new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();
            }
        });
        hd=	new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                BookInfo book= (BookInfo)msg.obj;

                //进度条消失
                mpd.dismiss();

                Intent intent=new Intent(MainActivity.this,BookView.class);
                //Bundle bd=new Bundle();
                //bundle.putSerializable(key,object);
                //bd.putSerializable(BookInfo.class.getName(),book);
                //intent.putExtras(bd);
                intent.putExtra(BookInfo.class.getName(),book);
                startActivity(intent);
            }
        };
    }
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if((result==null)||(result.getContents()==null))
        {
            return ;
        }
        mpd=new ProgressDialog(this);
        mpd.setMessage("请稍候，正在读取信息...");
        mpd.show();

        String urlstr="https://api.douban.com/v2/book/isbn/"+result.getContents();
        Log.i("OUTPUT",urlstr);
        //扫到ISBN后，启动下载线程下载图书信息
        new DownloadThread(urlstr).start();
    }

    private class DownloadThread extends Thread
    {
        String url=null;
        public DownloadThread(String urlstr)
        {
            url=urlstr;
        }
        public void run()
        {
            String result=Util.Download(url);
            Log.i("OUTPUT", "download over");
            BookInfo book=new Util().parseBookInfo(result);
            Log.i("OUTPUT", "parse over");
            Log.i("OUTPUT",book.getSummary()+book.getAuthor());
            //给主线程UI界面发消息，提醒下载信息，解析信息完毕

            Message msg=Message.obtain();
            msg.obj=book;
            hd.sendMessage(msg);
            Log.i("OUTPUT","send over");
        }
    }
}
