package com.example.safesteps;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;

public class GuardAccessibilityService extends AccessibilityService {
 private final Handler handler=new Handler(); private WindowManager windowManager; private LinearLayout overlay; private boolean blocking=false; private long lastBlockTime=0;
 @Override public void onServiceConnected(){super.onServiceConnected();windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);}
 @Override public void onAccessibilityEvent(AccessibilityEvent event){if(event==null)return;if(!getSharedPreferences(MainActivity.PREFS,0).getBoolean(MainActivity.ENABLED,false))return;String packageName=String.valueOf(event.getPackageName());if(getPackageName().equals(packageName))return;if(isForbidden(event,packageName))blockCurrentScreen();}
 private boolean isForbidden(AccessibilityEvent event,String packageName){
  String className=lower(event.getClassName()), text=lower(event.getText()), description=lower(event.getContentDescription()), allText=text+" "+description;
  if(packageName.equals("com.android.packageinstaller")||packageName.equals("com.google.android.packageinstaller")||packageName.equals("com.android.permissioncontroller")||packageName.equals("com.google.android.permissioncontroller"))return true;
  if(!packageName.equals("com.android.settings"))return false;
  if(className.contains("accessibility")||className.contains("installedaccessibility")||(className.contains("subsettings")&&(allText.contains("נגישות")||allText.contains("accessibility"))))return true;
  if(allText.contains("הגדרות אבטחה נוספות")||allText.contains("additional security settings")||allText.contains("additional security")||className.contains("securitysettings")||className.contains("security_settings"))return true;
  if(className.contains("deviceadmin")||className.contains("device_admin"))return true;
  if(allText.contains("מנהלי המכשיר")||allText.contains("מנהלי מכשירים")||allText.contains("אפליקציות שמנהלות את המכשיר")||allText.contains("יישומים שמנהלים את המכשיר")||allText.contains("אפליקציות מנהלות את המכשיר")||allText.contains("יישומים מנהלים את המכשיר")||allText.contains("אפליקציות שמנהלות מכשיר זה")||allText.contains("יישומים שמנהלים מכשיר זה")||allText.contains("device administrators")||allText.contains("device administrator")||allText.contains("device admin apps")||allText.contains("device admin"))return true;
  boolean appDetailsScreen=className.contains("appdetails")||className.contains("installedappdetails")||className.contains("manageapplications")||className.contains("applicationdetails")||className.contains("applicationinfo")||className.contains("appinfo");
  return appDetailsScreen&&(allText.contains("safe steps")||allText.contains("safesteps"));
 }
 private String lower(CharSequence v){return String.valueOf(v==null?"":v).toLowerCase(Locale.ROOT);}
 private String lower(List<CharSequence> values){if(values==null||values.isEmpty())return "";StringBuilder r=new StringBuilder();for(CharSequence v:values)if(v!=null){if(r.length()>0)r.append(' ');r.append(v);}return r.toString().toLowerCase(Locale.ROOT);}
 private void blockCurrentScreen(){long now=System.currentTimeMillis();if(blocking||now-lastBlockTime<100)return;lastBlockTime=now;blocking=true;showProtectionScreen();handler.postDelayed(()->performGlobalAction(GLOBAL_ACTION_BACK),0);}
 private void showProtectionScreen(){if(windowManager==null||overlay!=null){blocking=false;return;}LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(28),dp(28),dp(28),dp(28));root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(7,29,64),Color.rgb(34,112,188),Color.rgb(61,46,130)}));
  TextView shield=label("🛡",68,true);root.addView(shield,new LinearLayout.LayoutParams(-1,dp(105)));TextView title=label("הפעולה חסומה",29,true);root.addView(title,new LinearLayout.LayoutParams(-1,dp(60)));TextView message=label("מכשיר זה מוגן על ידי Safe Steps",17,false);root.addView(message,new LinearLayout.LayoutParams(-1,dp(55)));TextView waitMessage=label("המסך נסגר. יש להמתין שתי שניות…",15,false);root.addView(waitMessage,new LinearLayout.LayoutParams(-1,dp(55)));
  Button backButton=new Button(this);backButton.setText("חזור");backButton.setTextSize(17);backButton.setAllCaps(false);backButton.setEnabled(false);backButton.setOnClickListener(v->{performGlobalAction(GLOBAL_ACTION_HOME);removeProtectionScreen();});root.addView(backButton,new LinearLayout.LayoutParams(-1,dp(58)));
  WindowManager.LayoutParams params=new WindowManager.LayoutParams(-1,-1,WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_FULLSCREEN,PixelFormat.TRANSLUCENT);
  try{windowManager.addView(root,params);overlay=root;}catch(Exception e){blocking=false;return;}
  handler.postDelayed(()->{if(overlay!=root)return;backButton.setEnabled(true);waitMessage.setText("המסך נסגר. לחצו על חזור כדי לצאת.");},2000);
 }
 private TextView label(String t,float s,boolean b){TextView v=new TextView(this);v.setText(t);v.setTextSize(s);v.setTextColor(Color.WHITE);v.setGravity(Gravity.CENTER);if(b)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
 private int dp(float v){return(int)(v*getResources().getDisplayMetrics().density+0.5f);}
 private void removeProtectionScreen(){if(overlay!=null&&windowManager!=null)try{windowManager.removeView(overlay);}catch(Exception ignored){}overlay=null;blocking=false;}
 @Override public void onInterrupt(){removeProtectionScreen();}
 @Override public boolean onUnbind(Intent intent){removeProtectionScreen();return super.onUnbind(intent);}
}