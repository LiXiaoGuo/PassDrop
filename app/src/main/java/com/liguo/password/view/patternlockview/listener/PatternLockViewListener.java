package com.liguo.password.view.patternlockview.listener;


import com.liguo.password.view.patternlockview.PatternLockView;

import java.util.List;

/**
 * The callback interface for detecting patterns entered by the user
 */
public interface PatternLockViewListener {

    /**
     * Fired when the pattern drawing has just started
     */
    void onStarted();

    /**
     * Fired when the pattern is still being drawn and progressed to
     * one more {@link }
     */
    void onProgress(List<PatternLockView.Dot> progressPattern);

    /**
     * Fired when the user has completed drawing the pattern and has moved their finger away
     * from the view
     */
    void onComplete(List<PatternLockView.Dot> pattern);

    /**
     * Fired when the patten has been cleared from the view
     */
    void onCleared();
}
