package com.XYZ.Sampledemocode;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.yugasa.dishguru.fragments.Termsconditions;
import com.yugasa.dishguru.models.RecentToken;
import com.yugasa.dishguru.models.UserInfo;
import com.yugasa.dishguru.pushnotification.FcmInstanceIdService;
import com.yugasa.dishguru.utils.Apis;
import com.yugasa.dishguru.utils.Constants;
import com.yugasa.dishguru.utils.DroidPrefs;
import com.yugasa.dishguru.volley.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity
{

    //------ Initialization of variable of class-----------

    @InjectView(R.id.edit_login)
    EditText edit_login;
    @InjectView(R.id.edit_password)
    EditText edit_password;
    @InjectView(R.id.txt_register)
    TextView txt_register;
    private AlertDialog alertDialog;
    @InjectView(R.id.forgot_password)
    TextView forgot_password;
    @InjectView(R.id.layout_login)
    RelativeLayout layout_login;
    @InjectView(R.id.register)
    TextView register;
    @InjectView(R.id.terms)
    TextView terms;
    ProgressBar progressbarterms;
    String progress_text;


    //---------OnCreate method---------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        edit_login.getBackground().mutate().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        edit_password.getBackground().mutate().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

        //--------- get current EditText drawable-----------
        Drawable drawable = edit_login.getBackground(); 

        //--------- change the drawable color---------------
        drawable.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP); 

        //-----------OnclickListener method-----------------

        layout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(v.getWindowToken(), 2);
            }
        });


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_password.getText().clear();
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });


        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(edit_login.getText().toString()).matches()){
                    Toast.makeText(MainActivity.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                }
                else if(edit_password.getText().toString().isEmpty()||edit_password.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(MainActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();

                }
                else {
                    progress_text=getString(R.string.sign_in);
                    register();

                }
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Patterns.EMAIL_ADDRESS.matcher(edit_login.getText().toString()).matches()){
                    Toast.makeText(MainActivity.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                }
                else if(edit_password.getText().toString().isEmpty()||edit_password.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(MainActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();

                }
                else 
	            {
                    progress_text=getString(R.string.log_in);
                    register();

        	    }
  	        }
        });

        if(Build.VERSION.SDK_INT > 16) {
            edit_login.setBackground(drawable); // set the new drawable to EditText
        }else{
            edit_login.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
        Drawable drawable1 = edit_password.getBackground(); // get current EditText drawable
        drawable1.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if(Build.VERSION.SDK_INT > 16) {
            edit_password.setBackground(drawable); // set the new drawable to EditText
        }else{
            edit_password.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }
   
        SpannableString content = new SpannableString(getString(R.string.terms));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        terms.setText(content);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Terms and conditions");

                // ----------Set an EditText view to get user input-------------
                View view= LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_termsdialog,null);
                WebView wv = (WebView) view.findViewById(R.id.web);
                progressbarterms= (ProgressBar) view.findViewById(R.id.progressbarterms);
                wv.loadUrl("https://goo.gl/FeaIW2");
                WebSettings webSettings = wv.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv.setWebViewClient(new TermsClient());
                alert.setView(view);
                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //--------- Do something with value -----------------------------  
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });
    }

    //---------------OnResume Method---------------
    @Override
    protected void onResume() {
        super.onResume();
        String token= FirebaseInstanceId.getInstance().getToken();
        if(token!=null) {

            Log.i("token", token);
            RecentToken recentToken = new RecentToken();
            recentToken.token = token;
            DroidPrefs.apply(this, "token", recentToken);
        }

    }


    //------------Inner Class---------------------------


    class TermsClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
                        progressbarterms.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

                        progressbarterms.setVisibility(View.GONE);
        }
    }

    //---------------Register Method Calling API---------------------

    public void register()
	{
		if(Constants.isInternetConnected(this)){
 	   	String url= Apis.BASE_URL+"user-signup";

		RecentToken recentToken=DroidPrefs.get(this,"token",RecentToken.class);
		Log.i("url",url);
		final ProgressDialog progressDialog=new ProgressDialog(this);
	        progressDialog.setMessage(progress_text);
		progressDialog.setCancelable(false);
		progressDialog.show();
		JSONObject jsonObject=new JSONObject();
	    try {
		jsonObject.put("email",edit_login.getText().toString().trim());
		jsonObject.put("password",edit_password.getText().toString().trim());
		if(recentToken!=null &&recentToken.token!=null){
		    jsonObject.put("device_id",recentToken.token) ;
		}
		else{
		    jsonObject.put("device_id", FcmInstanceIdService.recent_token) ;
		}
		jsonObject.put("device","0");
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>()
        {
		  @Override
		  public void onResponse(JSONObject response) 
		  {
		      progressDialog.dismiss();
    		  if(response!=null)
    		  {
    	           try 
    	           {
    		          if(response.getString("status").equalsIgnoreCase("1"))
                      {
    		              UserInfo userInfo=new UserInfo();
    		              JSONArray jsonArray=response.getJSONArray("user_detail");
    		              if(jsonArray!=null &&jsonArray.length()>0)
                          {
    		                  JSONObject result=jsonArray.getJSONObject(0);
    	                      userInfo.id=result.getString("id");
        		              userInfo.email=result.getString("email");
            		          userInfo.usertoken=result.getString("usertoken");
            		          userInfo.image=result.getString("image");
            		          userInfo.isactive=result.getString("isactive");
            		          DroidPrefs.apply(MainActivity.this,"user",userInfo);
            		          if(userInfo.isactive.equalsIgnoreCase("3")){
            		              startActivity(new Intent(MainActivity.this,HomeActivity.class));
            		              finish();
            		          }
                              else
            		          {
                                Intent intent=new Intent(MainActivity.this,ResendScreen.class);
                                intent.putExtra("email",edit_login.getText().toString().trim());
                                intent.putExtra("password",edit_password.getText().toString().trim());
                                startActivity(intent);
                               }
                          }

                      }
                      else
    	              {
                        Toast.makeText(MainActivity.this, R.string.register_message, Toast.LENGTH_SHORT).show();
                      }
                    } 
                    catch (JSONException e) 
                    {
                        e.printStackTrace();
                    }
              }
          }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.
            DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 30000));
        }
    }
}