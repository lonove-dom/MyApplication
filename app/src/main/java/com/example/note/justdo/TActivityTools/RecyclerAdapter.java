package com.example.note.justdo.TActivityTools;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.note.justdo.R;
import com.example.note.justdo.TActivityTools.Eventview;

import java.util.List;

/**
 * Created by Choz on 2018/4/5.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    LayoutInflater mInflate;
    List<Eventview> mEventview;
    MviewOnClickListener mviewOnClickListener;
    final int ISVIEW = 0;
    final int ISADDVIEW = 1;
    Boolean allowdelete = false;
    Animation anim;
    Boolean animstopped = false;
    RecyclerView recyclerView;

    public RecyclerAdapter(Context context, LayoutInflater mInflate, List<Eventview> mData) {
        this.mContext = context;
        this.mInflate = mInflate;
        this.mEventview = mData;
        anim = AnimationUtils.loadAnimation(mContext, R.anim.viewanim);
    }

    public void setAllowdelete(Boolean allowdelete) {
        this.allowdelete = allowdelete;
    }

    public Boolean getAllowdelete() {
        return allowdelete;
    }

    public void setAnimstopped(Boolean animstopped) {
        this.animstopped = animstopped;
    }

    public void setMviewOnClickListener(MviewOnClickListener mviewOnClickListener) {
        this.mviewOnClickListener = mviewOnClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mEventview.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ISVIEW) {
            View view = mInflate.inflate(R.layout.viewitem, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        } else {
            View view = mInflate.inflate(R.layout.addbtnlayout, parent, false);
            MyaddViewHolder myaddViewHolder = new MyaddViewHolder(view);
            return myaddViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final int pos = holder.getLayoutPosition();
        View.OnClickListener viewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mviewOnClickListener.viewOnClickListener(v, pos);
            }
        };
        View.OnClickListener interceptClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };
        if (holder instanceof MyViewHolder) {
            Log.d("TAG","pos="+pos);
            ((MyViewHolder) holder).button.setBackground(mEventview.get(position).getBackground());
            ((MyViewHolder) holder).editText.setText(mEventview.get(position).getTitle());
            ((MyViewHolder) holder).button.setTransitionName(Integer.toString(pos+1));
            ((MyViewHolder) holder).button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (allowdelete) {
                        mviewOnClickListener.moveOnClickListener(holder);
                    }
                    return false;
                }
            });
            ((MyViewHolder) holder).button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mviewOnClickListener.OnstartdeleteListener(v, holder, position);
                    return true;
                }
            });
            ((MyViewHolder) holder).detbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mviewOnClickListener.deleteviewOnClickListener(v, pos);
                }
            });
            ((MyViewHolder) holder).button.setOnClickListener(viewClickListener);
            ((MyViewHolder) holder).editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    mviewOnClickListener.edtfinishActionListener(i, holder, position);
                    return true;
                }
            });
            if (!allowdelete) {
                ((MyViewHolder) holder).detbutton.setVisibility(View.INVISIBLE);
                if (((MyViewHolder) holder).getNeedstopanim()) {
                    ((MyViewHolder) holder).button.clearAnimation();
                    ((MyViewHolder) holder).button.setOnClickListener(viewClickListener);
                }
                ((MyViewHolder) holder).setNeedstopanim(false);
            } else {
                ((MyViewHolder) holder).detbutton.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).button.setOnClickListener(interceptClickListener);
                ((MyViewHolder) holder).button.startAnimation(anim);
                ((MyViewHolder) holder).setNeedstopanim(true);
            }
        } else if (holder instanceof MyaddViewHolder) {
            ((MyaddViewHolder) holder).button.setBackground(mEventview.get(position).getBackground());
            ((MyaddViewHolder) holder).button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mviewOnClickListener.viewOnClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mEventview.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        Button button;
        Button detbutton;
        public EditText editText;
        Boolean needstopanim = false;

        public MyViewHolder(View itemView) {
            super(itemView);
            constraintLayout = (ConstraintLayout) itemView;
            button = constraintLayout.findViewById(R.id.tbutton);
            editText = constraintLayout.findViewById(R.id.tedit);
            detbutton = constraintLayout.findViewById(R.id.detbutton);
        }

        public void setNeedstopanim(boolean needstopanim) {
            this.needstopanim = needstopanim;
        }

        public Boolean getNeedstopanim() {
            return needstopanim;
        }
    }

    public class MyaddViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        Button button;

        public MyaddViewHolder(View itemView) {
            super(itemView);
            linearLayout = (LinearLayout) itemView;
            button = linearLayout.findViewById(R.id.addview);
        }
    }

    public interface MviewOnClickListener {
        void viewOnClickListener(View view, int pos);

        void edtfinishActionListener(int keyinput, RecyclerView.ViewHolder holder, int pos);

        void OnstartdeleteListener(View view, RecyclerView.ViewHolder viewHolder, int pos);

        void deleteviewOnClickListener(View view, int pos);

        void moveOnClickListener(RecyclerView.ViewHolder holder);
    }
}
