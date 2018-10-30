package com.example.note.justdo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.note.justdo.TActivityTools.Eventview;
import com.example.note.justdo.TActivityTools.MviewitemDecoration;
import com.example.note.justdo.TActivityTools.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/*
   @多列表activity
 */
public class TActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    //列表集合
    List<Eventview> eventviews;
    //添加按钮
    Button addbtn;
    //总计数特殊事件（unique，listid=0）
    Event cev;
    //当前选中列表的标志事件
    Event preev;
    //用于遍历的辅助事件；
    Event cusorev;
    //获取数据库总管理
    Eventdaomanger eventdaomanger = getApp().getEventdaomanger();
    //数据库管理
    DaoSession daoSession = eventdaomanger.getDaoSession();
    //最基层事件数据管理者
    EventDao eventDao = daoSession.getEventDao();
    //用于筛选列表头事件的标志字符串
    String symbolstr = "SYMBOL STR";
    //列表计数（从1开始）
    int viewnum;
    //当前列表的id
    int clistid = getApp().getListid();
    //编辑完成按钮
    Button donebtn;
    //获取当前列表的背景（从主界面生成）
    Bitmap cbackground = getApp().getViewbackground();
    Bitmap voidBitmap=getApp().getVoidviewbackground();
    private static Drawable voidbackground;
    //bitmap转换类
    imageConverter imageConverter;
    //recyclerview手势辅助
    ItemTouchHelper helper;
    //recyclerview布局管理
    GridLayoutManager gridLayoutManager;
    
   // ScreenShot screenShot;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t);
        final Intent enterIntent = new Intent(TActivity.this, MainActivity.class);
    //    final View constraintLayout = getLayoutInflater().inflate(R.layout.activity_t, null, false);
        //screenshot用法探索中...暂且无用
      //  screenShot = new ScreenShot();
        //用于结束长按拖动、删除界面
        donebtn = findViewById(R.id.Donebtn);
        //初始不可见，只有处于长按拖动状态时可见
        donebtn.setVisibility(View.INVISIBLE);
        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //完成后刷新并返回初始界面
                adapter.setAllowdelete(false);
                adapter.notifyDataSetChanged();
                donebtn.setVisibility(View.INVISIBLE);
            }
        });
        eventviews = new ArrayList<>();
        imageConverter = new imageConverter();
        voidbackground=imageConverter.BitmapToDrawable(voidBitmap);//默认背景
        cev = daoSession.getEventDao().queryBuilder().where(EventDao.Properties.Listid.eq(0)).unique();//总标识工具event
   /*     if(cev==null){
            cev=new Event(null,"charge",0,"chargeev",1) ;
            eventDao.insert(cev);
            preev = new Event(symbolstr,  1, "first");
            eventDao.insert(preev);
        }*/
        if(cbackground!=null){
            //根据当前listid获取对应标识event
        preev = eventDao.queryBuilder().where(EventDao.Properties.Listid.eq(clistid), EventDao.Properties.Context.eq(symbolstr)).unique();
        preev.setBackgroundString(imageConverter.BitmapToString(cbackground));//设置背景
        eventDao.update(preev);}//更新
        viewnum = cev.getListnum();//获取多列表数目
        for (int i = 0; i <= viewnum; i++) {
            if (i < viewnum) {//前viewnum个eventview为已存在的列表
                cusorev = eventDao.queryBuilder().where(EventDao.Properties.Listid.eq(i + 1), EventDao.Properties.Context.eq(symbolstr)).unique();
                eventviews.add(new Eventview(imageConverter.StringToDrawable(cusorev.getBackgroundString()), cusorev.getListtitle(), 0));
            } else {//最后一个为添加按钮
                eventviews.add(new Eventview(1, getDrawable(R.drawable.ic__addviewbtn)));
            }
        }
        adapter = new RecyclerAdapter(this, getLayoutInflater(), eventviews);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new MviewitemDecoration());
        adapter.setMviewOnClickListener(new RecyclerAdapter.MviewOnClickListener() {
            @Override
            //view点击监听
            public void viewOnClickListener(View view, final int pos) {
                if (eventviews.get(pos).getType() == 0) {//正常的列表view
                    getApp().setListid(pos + 1);//设置对应listid
                   // getApp().setListsViewBackground(imageConverter.ScreenShotTObitmap(TActivity.this));//暂时无用
                    Log.d("TAG","VIEWtransitionname="+view.getTransitionName());
                    startActivity(enterIntent, ActivityOptionsCompat.makeScaleUpAnimation(view,view.getWidth()/2,view.getHeight()/2,view.getWidth(),view.getHeight()).toBundle());//跳转至主界面

                  //  startActivity(enterIntent, ActivityOptionsCompat.makeScaleUpAnimation(view,view.getWidth()/2,view.getHeight()/2,0,0).toBundle());//跳转至主界面
                } else if (eventviews.get(pos).getType() == 1) {//添加按钮
                    eventviews.get(pos).setType(0);//将当前view转化为正常列表view
                    eventviews.get(pos).setBackground(voidbackground);
                    viewnum++;//列表数+1
                    cev.setListnum(viewnum);//更新数据库信息
                    eventDao.update(cev);
                    preev = new Event(symbolstr, viewnum, null);//新listid标识event
                    preev.setBackgroundString(imageConverter.BitmapToString(voidBitmap));
                    //跳出新列表标题输入框
                    AlertDialog.Builder builder = new AlertDialog.Builder(TActivity.this);
                    builder.setTitle("请输入标题");
                    //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                    View editview = LayoutInflater.from(TActivity.this).inflate(R.layout.editidialog, null);
                    //    设置我们自己定义的布局文件作为弹出框的Content
                    builder.setView(editview);
                    final EditText editText = editview.findViewById(R.id.editdialog);
                    /*editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            if(actionId==EditorInfo.IME_ACTION_DONE){
                                _ALWAYS);
                            }
                            return true;
                        }
                    });*/
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (editText.getText().toString() != null) {
                                eventviews.get(pos).setTitle(editText.getText().toString());//eventview设置标题
                                preev.setListtitle(editText.getText().toString());//数据库更新标识event
                                adapter.notifyItemChanged(pos);
                                eventDao.update(preev);
                                editText.setText(null);
                            }
                            InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            im.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        }
                    });
                    builder.show();
                    ((InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
                    //          adapter.notifyItemChanged(pos);
                    eventDao.insert(preev);
                    eventviews.add(new Eventview(1, getDrawable(R.drawable.ic__addviewbtn)));
                    adapter.notifyItemChanged(pos + 1);
                }
            }

            @Override
            //每个view中的edittext回车监听
            public void edtfinishActionListener(int keyinput, RecyclerView.ViewHolder holder, int pos) {
                if (keyinput == EditorInfo.IME_ACTION_DONE) {
                    EditText editText = ((RecyclerAdapter.MyViewHolder) holder).editText;
                    String title = editText.getText().toString();
                    eventviews.get(pos).setTitle(title);
                    ((RecyclerAdapter.MyViewHolder) holder).editText.setText(title);
                    adapter.notifyItemChanged(pos);
                    InputMethodManager im = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    editText.setCursorVisible(false);
                    im.hideSoftInputFromWindow(TActivity.this.getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    preev = eventDao.queryBuilder().where(EventDao.Properties.Listid.eq(pos + 1), EventDao.Properties.Context.eq(symbolstr)).unique();
                    preev.setListtitle(title);
                    eventDao.update(preev);
                }

            }

            @Override
            public void OnstartdeleteListener(View view, RecyclerView.ViewHolder holder, int pos) {
                if (!adapter.getAllowdelete()) {//长按后如果之前不是抖动删除状态，则进入该状态
                    donebtn.setVisibility(View.VISIBLE);
                    adapter.setAllowdelete(true);
                    adapter.notifyDataSetChanged();//全局刷新
                }
                //  helper.startDrag(recyclerView.getChildViewHolder(gridLayoutManager.findViewByPosition(pos)));
                helper.startDrag(holder);//长按拖动开启
                Log.d("TAG", "START DRAG=========");
            }

            @Override
            //view中的删除按钮点击监听
            public void deleteviewOnClickListener(View view, int pos) {
                if (pos == 0 && viewnum == 1) {//如果删除时只剩一个view
                    eventdaomanger.deleteButRemainfirst(pos + 1);//删除数据，并新建默认列表项，因为至少应有一个列表项
                    eventviews.get(pos).setTitle("first");
                    eventviews.get(pos).setBackground(null);
                    adapter.notifyItemChanged(pos);
                } else {//否则进行数据删除，并更新数据库信息
                    eventdaomanger.deleteBylistid(pos + 1);
                    if (viewnum > pos + 1) {
                        for (int i = pos + 2; i <= viewnum; i++) {
                            eventdaomanger.updateReducedlistid(i);
                        }
                    }
                    eventviews.remove(pos);
                    Log.d("TAG", "pos=" + pos);
                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeChanged(pos, ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() - pos + 1);
                    Log.d("TAG", "pos=" + pos);
                    viewnum--;
                    cev.setListnum(viewnum);//更新总标识event
                    eventDao.update(cev);
                }
            }

            @Override
            public void moveOnClickListener(RecyclerView.ViewHolder holder) {
                helper.startDrag(holder);
                Log.d("TAG", "start click drag");
            }
        });
        //为RecycleView绑定触摸事件
        helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //拖动事件
                if (target.getAdapterPosition() != viewnum && viewHolder.getAdapterPosition() != target.getAdapterPosition()) {
                    Log.d("TAG", "position==" + target.getAdapterPosition());
                    List<Event> startlist = eventdaomanger.queryEventList(viewHolder.getAdapterPosition() + 1);
                    if (target.getAdapterPosition() > viewHolder.getAdapterPosition()) {
                        for (int i = viewHolder.getAdapterPosition() + 2; i <= target.getAdapterPosition() + 1; i++) {
                            eventdaomanger.updateReducedlistid(i);
                        }
                    } else {
                        for (int i = viewHolder.getAdapterPosition(); i >= target.getAdapterPosition() + 1; i--) {
                            eventdaomanger.updateIncreasedlistid(i);
                        }
                    }
                    eventdaomanger.updateSwapedlistid(startlist, target.getAdapterPosition() + 1);
                    //     eventdaomanger.updateSwapedlistid(viewHolder.getAdapterPosition()+1,target.getAdapterPosition()+1);
                    //             Collections.swap(eventviews,viewHolder.getAdapterPosition(),target.getAdapterPosition());
                    Eventview eventview = eventviews.get(viewHolder.getAdapterPosition());
                    eventviews.remove(eventview);
                    eventviews.add(target.getAdapterPosition(), eventview);
                    adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                    Log.d("TAG", "has been moved from" + viewHolder.getAdapterPosition() + "to position" + target.getAdapterPosition());
                    adapter.notifyItemRangeChanged(Math.min(target.getAdapterPosition(), viewHolder.getAdapterPosition()) + 1, Math.abs(viewHolder.getAdapterPosition() - target.getAdapterPosition()) - 1);
                    //            eventdaomanger.updateSwapedlistid(viewHolder.getAdapterPosition()+1,target.getAdapterPosition()+1);
                    return true;
                }
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否可拖拽
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

        });
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {

    }

    private App getApp() {
        return App.getInstance();
    }
}
