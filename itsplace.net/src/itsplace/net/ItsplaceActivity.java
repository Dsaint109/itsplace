package itsplace.net;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import itsplace.net.common.TestMenu;
import itsplace.net.user.LoginActivity;
import itsplace.net.user.LoginAsyncActivity;
import itsplace.net.user.SignUpActivity;

import itsplace.net.util.Encrypt;
import itsplace.net.util.L;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.itsplace.domain.User;

public class ItsplaceActivity extends Activity {
 
	protected static final String TAG = ItsplaceActivity.class.getSimpleName();
	private User user;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		L.i(TAG, "onCreate");
		// splash 및 초기화 작업
		startActivity(new Intent(this, SplashActivity.class));

	//	TestMenu header = (TestMenu) findViewById(R.id.header);
	 //   header.initHeader();
		
		Button btn = (Button) findViewById(R.id.btnCustomTab);
		final Intent intent = new Intent(this, MainActivity.class);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "작업이 끝났습니다.", Toast.LENGTH_LONG).show();
				startActivity(intent);

				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// main.getUser().getEmail(),1000).show();
				/*
				 * try { // The URL for making the GET request final String url
				 * = getString(R.string.base_uri) + "/state/{abbreviation}";
				 * 
				 * Log.i("","유알엘i:"+url);
				 * 
				 * // Set the Accept header for "application/json" HttpHeaders
				 * requestHeaders = new HttpHeaders(); List<MediaType>
				 * acceptableMediaTypes = new ArrayList<MediaType>();
				 * acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
				 * requestHeaders.setAccept(acceptableMediaTypes);
				 * 
				 * // Populate the headers in an HttpEntity object to use for
				 * the // request HttpEntity<?> requestEntity = new
				 * HttpEntity<Object>(requestHeaders);
				 * 
				 * // Create a new RestTemplate instance RestTemplate
				 * restTemplate = new RestTemplate();
				 * 
				 * ResponseEntity<State> responseEntity =
				 * restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				 * State.class, "al");
				 * 
				 * 
				 * Toast.makeText(getApplicationContext(),
				 * responseEntity.getBody().toString(),
				 * Toast.LENGTH_LONG).show(); // convert the array to a list and
				 * return it // return Arrays.asList(responseEntity.getBody());
				 * } catch (Exception e) { // Log.e(TAG, e.getMessage(), e); }
				 */
			}
		});
	}

	@Override
	protected void onStart() {
		
		super.onStart();
		L.i(TAG, "onStart 흡니다");
		init();
	}
	@Override
	protected void onResume() {
		super.onResume();
		L.i(TAG, "onResume 합니다");
		init();
	}
	
	private void init(){
		MainApplication main = (MainApplication) getApplication();
		user = main.getUser();
		Intent intent;
		if(main.isLogged()){
			L.i(TAG, "로그인중입니다");
		}else{
			
			L.i(TAG, "로그인중  아닙니다");
			if (user == null) {
				L.i(TAG, "회원가입");
				// startActivityForResult(new Intent(this,LoginActivity.class),
				// 9999);
				startActivity(new Intent(this, SignUpActivity.class));
				
			}else if(user.getPassword()==null || user.getPassword()==""){
				intent = new Intent(this, LoginActivity.class);
				intent.putExtra("Email", user.getEmail());
				startActivity(intent);
				
			}else if (user.getEmail().length() > 0 && user.getPassword().length() > 0) {
				try {
					L.i(TAG,"자동로그인 password:"+ user.getPassword());
	
					user.setPassword(Encrypt.decrypt("itsplace", user.getPassword()));
	
					//new LoginAsyncActivity(getApplicationContext(), user);
					startActivity(new Intent(this,(LoginAsyncActivity.class)));
	
				} catch (Exception e) {
					// 앱이 종료되지 않았다면 main의 user는 이미 복호화 되어있음..
					L.i(TAG+"자동로그인 exception", e.getMessage(), e);
					
				}
	
			}
		}
	}
	
}