package com.localhostloader.business.callbacks;
import com.localhostloader.business.model.InstallResult;
public interface InstallCallback {
    void onInstallComplete(InstallResult result);
}
