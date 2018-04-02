package com.starssoft.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: HRF
 * Date: 2017/6/20 0020
 * Time: 下午 4:00
 * Description: Too
 */

public class MySnakeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private Canvas canvas;
    private boolean isRuning;
    private Path path;

    private Paint paint;
    private Paint paint1;
    private Context context;
    private int measuredWidth, measuredHeight;
    private int width;

    public static final int T = 0;
    public static final int L = 1;
    public static final int R = 2;
    public static final int B = 3;
    private int orientation = T;

    private List<Rect> list = new ArrayList<>();
    private List<OR> listCa = new ArrayList<>();

    private boolean isOver;

    private int wScore = 24;

    private boolean isPause;
    private long speed = 300;
    private boolean isAddRect = true;

    public MySnakeView(Context context) {
        super(context);
        init(context);
    }

    public MySnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        thread = new Thread(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);

        paint = new Paint();
        path = new Path();
        Log.i("My", "init");
        this.context = context;
        paint = new Paint();
        paint1 = new Paint();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("My", "" + getWidth());
                Log.i("My", "" + getHeight());
                measuredWidth = getWidth();
                measuredHeight = getHeight();
                width = measuredWidth / wScore;
                initData();
                MySnakeView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void initData() {
        int size = measuredHeight / width;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < wScore; j++) {
                Rect rect = new Rect(j * width, i * width, (j + 1) * width, (i + 1) * width);
                list.add(rect);
            }
        }
        for (int i = 10; i >= 0; i--) {
            OR or = new OR();
            or.rect = list.get(list.size() - wScore / 2 - i * wScore);
            or.orientation = T;
            listCa.add(or);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRuning = true;
        thread.start();
        Log.i("My", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("My", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRuning = false;
        Log.i("My", "surfaceDestroyed");
    }

    private void isOrientation() {
        listCa.remove(listCa.size() - 1);//删除最后一个
        Rect firstRect = listCa.get(0).rect;//最后一个
        OR or = new OR();
        Rect rect = null;
        switch (orientation) {
            case L:
                rect = new Rect(firstRect.left - width, firstRect.top, firstRect.right - width, firstRect.bottom);
                or.orientation = L;
                break;
            case T:
                rect = new Rect(firstRect.left, firstRect.top - width, firstRect.right, firstRect.bottom - width);
                or.orientation = T;
                break;
            case R:
                rect = new Rect(firstRect.left + width, firstRect.top, firstRect.right + width, firstRect.bottom);
                or.orientation = R;
                break;
            case B:
                rect = new Rect(firstRect.left, firstRect.top + width, firstRect.right, firstRect.bottom + width);
                or.orientation = B;
                break;
        }
        or.rect = rect;
        switch (orientation) {
            case L:
                if (or.rect.left < 0) {
                    isOver = true;
                    if (gameViewInterface != null) {
                        Log.i("D", "L");
                        gameViewInterface.over(listCa.size() - 10);
                    }
                }
                break;
            case T:
                if (or.rect.top < 0) {
                    isOver = true;
                    if (gameViewInterface != null) {
                        Log.i("D", "T");
                        gameViewInterface.over(listCa.size() - 10);
                    }
                }
                break;
            case R:
                if (or.rect.right > width * wScore) {
                    isOver = true;
                    if (gameViewInterface != null) {
                        Log.i("D", "R");
                        gameViewInterface.over(listCa.size() - 10);
                    }
                }
                break;
            case B:
                if (or.rect.bottom > measuredHeight / width * width) {
                    isOver = true;
                    if (gameViewInterface != null) {
                        Log.i("D", "B");
                        gameViewInterface.over(listCa.size() - 10);
                    }
                }
                break;
        }

        for (int i = 0; i < listCa.size(); i++) {
            if (isOver(listCa.get(i).rect, or.rect)) {
                isOver = true;
                if (gameViewInterface != null) {
                    Log.i("D", "C");
                    gameViewInterface.over(listCa.size() - 10);
                }
                return;
            }
        }

        if (!isOver) {
            or.rect = rect;
            listCa.add(0, or);
        }
    }

    private boolean isOver(Rect rect, Rect rect1) {
        if (rect.left == rect1.left && rect.top == rect1.top && rect.right == rect1.right && rect.bottom == rect1.bottom) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Log.i("My", "run");

        while (isRuning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 150) {
                try {
                    Thread.sleep(150 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        Log.i("My", "draw");
        try {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.parseColor("#B7B7B7"));
            paint.setColor(Color.BLUE);
            paint1.setColor(Color.RED);
            paint1.setStyle(Paint.Style.STROKE);
            OR or = listCa.get(0);
            if (rect != null && or.rect.left == rect.left && or.rect.top == rect.top && or.rect.right == rect.right && or.rect.bottom == rect.bottom) {
                isAddRect = true;
                or = listCa.get(listCa.size() - 1);
                OR or1 = new OR();
                Rect rect1 = new Rect();
                switch (or.orientation) {
                    case L:
                        rect1.left = or.rect.left + width;
                        rect1.top = or.rect.top;
                        rect1.right = or.rect.right + width;
                        rect1.bottom = or.rect.bottom;
                        or1.orientation = L;
                        break;
                    case T:
                        rect1.left = or.rect.left;
                        rect1.top = or.rect.top + width;
                        rect1.right = or.rect.right;
                        rect1.bottom = or.rect.bottom + width;
                        or1.orientation = T;
                        Log.i("My", or1 + "");
                        break;
                    case R:
                        rect1.left = or.rect.left - width;
                        rect1.top = or.rect.top;
                        rect1.right = or.rect.right - width;
                        rect1.bottom = or.rect.bottom;
                        or1.orientation = R;
                        break;
                    case B:
                        rect1.left = or.rect.left;
                        rect1.top = or.rect.top - width;
                        rect1.right = or.rect.right;
                        rect1.bottom = or.rect.bottom - width;
                        or1.orientation = B;
                        break;
                }
                or1.rect = rect1;
                listCa.add(or1);
            }
            for (int i = 0; i < listCa.size(); i++) {
                Log.i("My", "i" + i + " " + listCa.get(i).rect);
                canvas.drawRect(listCa.get(i).rect, paint);
            }

            if (isAddRect) {
                addRect();
                isAddRect = false;
            }
            canvas.drawRect(rect, paint);
            for (int i = 0; i < list.size(); i++) {
                canvas.drawRect(list.get(i), paint1);
            }
            if (!isPause && !isOver)
                isOrientation();
        } catch (Exception e) {

        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    Rect rect;

    private void addRect() {
        Random random = new Random();
        int h;
        int w;
        while (true) {
            h = random.nextInt(measuredHeight / width) * width;
            w = random.nextInt(wScore) * width;
            int is = 0;
            for (int i = 0; i < listCa.size(); i++) {
                Rect rect = listCa.get(i).rect;
                if (rect.left != w && rect.top != h && rect.right != w + width && rect.bottom != h + width) {
                    is++;
                }
            }
            if (is >= listCa.size()) {
                break;
            }
        }
        Log.i("My", "l,t,r,w" + ":" + w + " " + h + " " + (w + width) + " " + (h + width));
        rect = new Rect(w, h, w + width, h + width);
    }

    public void l() {
        if (orientation == T || orientation == B) {
            orientation = L;
        }
    }

    public void t() {
        if (orientation == L || orientation == R) {
            orientation = T;
        }
    }

    public void r() {
        if (orientation == T || orientation == B) {
            orientation = R;
        }
    }

    public void b() {
        if (orientation == L || orientation == R) {
            orientation = B;
        }
    }

    public interface GameViewInterface {
        void over(int sum);
    }

    private GameViewInterface gameViewInterface;

    public void setGameViewInterface(GameViewInterface gameViewInterface) {
        this.gameViewInterface = gameViewInterface;
    }

    public void isPause(boolean isPause) {
        this.isPause = isPause;
    }

    public void setSpeed(long s) {
        speed = s;
    }
}
