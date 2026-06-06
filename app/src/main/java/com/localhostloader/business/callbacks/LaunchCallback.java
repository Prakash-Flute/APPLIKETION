package com.localhostloader.business.callbacks;
import com.localhostloader.business.model.LaunchResult;
public interface LaunchCallback {
    void onLaunchPrepared(LaunchResult result);
}
