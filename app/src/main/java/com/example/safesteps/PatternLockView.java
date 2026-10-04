package com.example.safesteps;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.*;
public class PatternLockView extends View {
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final List<Integer> pts=new ArrayList<>(); private float r;
 public PatternLockView(Context c){super(c);p.setStrokeWidth(5);setBackgroundColor(Color.TRANSPARENT);}
 protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();r=Math.min(w,h)/12f;float ox=w/2,oy=h/2,step=Math.min(w,h)/4f;
  p.setStyle(Paint.Style.STROKE);p.setColor(Color.rgb(40,125,225));p.setStrokeWidth(6);
  for(int i=0;i<pts.size()-1;i++){int a=pts.get(i)-1,b=pts.get(i+1)-1;c.drawLine(ox+(a%3-1)*step,oy+(a/3-1)*step,ox+(b%3-1)*step,oy+(b/3-1)*step,p);}
  p.setStyle(Paint.Style.FILL);for(int i=1;i<=9;i++){int x=(i-1)%3,y=(i-1)/3;p.setColor(pts.contains(i)?Color.rgb(40,125,225):Color.LTGRAY);c.drawCircle(ox+(x-1)*step,oy+(y-1)*step,r,p);}
 }
 public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_DOWN&&e.getAction()!=MotionEvent.ACTION_MOVE)return true;float w=getWidth(),h=getHeight(),step=Math.min(w,h)/4f,ox=w/2,oy=h/2;int col=Math.round((e.getX()-ox)/step)+1,row=Math.round((e.getY()-oy)/step)+1;if(col>=0&&col<=2&&row>=0&&row<=2){int n=row*3+col+1;if(!pts.contains(n)){pts.add(n);invalidate();}}return true;}
 public String getPattern(){StringBuilder s=new StringBuilder();for(int n:pts)s.append(n);return s.toString();}
 public void clearPattern(){pts.clear();invalidate();}
}