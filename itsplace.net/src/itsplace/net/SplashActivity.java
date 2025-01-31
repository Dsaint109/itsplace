package itsplace.net;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        initialize();
    }

    private void initialize()
    {
        Handler handler =  new Handler()
        {
             @Override
             public void handleMessage(Message msg)
             {
                 finish();    	// 액티비티 종료
             }
        };

        handler.sendEmptyMessageDelayed(0, 2500);	// ms, 3초후 종료시킴
    }
}
