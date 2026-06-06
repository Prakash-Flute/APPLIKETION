package com.localhostloader.business.callbacks;
import com.localhostloader.business.model.UninstallResult;
public interface UninstallCallback {
    void onUninstallComplete(UninstallResult result);
}
