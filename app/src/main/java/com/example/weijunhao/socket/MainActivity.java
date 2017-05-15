package com.example.weijunhao.socket;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private EditText mEditText;
    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //1.创建监听指定服务器地址以及指定服务器监听的端口号
                            Socket socket = new Socket("192.168.1.110", 12306);
                            //2.拿到客户端的socket对象的输出流发送给服务器数据
                            OutputStream os = socket.getOutputStream();
                            //写入要发送给服务器的数据
                            os.write(mEditText.getText().toString().getBytes());
                            os.flush();
                            socket.shutdownOutput();;

                            //拿到socket的输入流，这里存储的是服务器返回的数据
                            InputStream is = socket.getInputStream();
                            //解析服务器返回的数据
                            InputStreamReader reader = new InputStreamReader(is);
                            BufferedReader bufReader = new BufferedReader(reader);
                            String s = null;
                            final StringBuffer sb = new StringBuffer();
                            while((s = bufReader.readLine()) != null){
                                sb.append(s);
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTextView.setText(mTextView.getText() + sb.toString());
                                }
                            });
                            //3、关闭IO资源（注：实际开发中需要放到finally中）
                            bufReader.close();
                            reader.close();
                            is.close();
                            os.close();
                            socket.close();

                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}
