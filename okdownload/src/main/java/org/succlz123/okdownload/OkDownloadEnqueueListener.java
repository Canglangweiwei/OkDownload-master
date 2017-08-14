package org.succlz123.okdownload;


@SuppressWarnings("ALL")
public interface OkDownloadEnqueueListener extends OkDownloadCancelListener {

    void onStart(int id);

    void onProgress(int progress, long cacheSize, long totalSize);

    void onRestart();

    void onPause();

    void onFinish();
}
