package org.succlz123.okdownload;


@SuppressWarnings("ALL")
public interface OkDownloadCancelListener {

    void onCancel();

    void onError(OkDownloadError error);
}
