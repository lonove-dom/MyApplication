package com.example.note.justdo.TimeReminder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.example.note.justdo.TimeReminder.TimeJob;

/**
 * Created by Choz on 2018/8/22.
 * 后台任务创建类
 */

public class TimeJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        return new TimeJob();
    }
}
