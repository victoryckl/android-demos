package com.example.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.book.BookInfo;

/**
 * Created by Jim on 13-7-10.
 */
public class BookView extends Activity {
    private Intent intent;
    private TextView title,author,publisher,date,isbn,summary;
    private ImageView cover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bookview);

        title=(TextView)findViewById(R.id.bookview_title);
        author=(TextView)findViewById(R.id.bookview_author);
        publisher=(TextView)findViewById(R.id.bookview_publisher);
        date=(TextView)findViewById(R.id.bookview_publisherdate);
        isbn=(TextView)findViewById(R.id.bookview_isbn);
        summary=(TextView)findViewById(R.id.bookview_summary);
        cover=(ImageView)findViewById(R.id.bookview_cover);

        intent=getIntent();
        //BookInfo book=(BookInfo) getIntent().getSerializableExtra(BookInfo.class.getName());
        BookInfo book=(BookInfo)intent.getParcelableExtra(BookInfo.class.getName());

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        date.setText(book.getPublishDate());
        isbn.setText(book.getISBN());
        summary.setText(book.getSummary());
        cover.setImageBitmap(book.getBitmap());
    }
}
