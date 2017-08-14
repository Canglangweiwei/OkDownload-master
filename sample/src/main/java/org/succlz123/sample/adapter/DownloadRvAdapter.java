package org.succlz123.sample.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.succlz123.okdownload.OkDownloadEnqueueListener;
import org.succlz123.okdownload.OkDownloadError;
import org.succlz123.okdownload.OkDownloadManager;
import org.succlz123.okdownload.OkDownloadRequest;
import org.succlz123.sample.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
public class DownloadRvAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mDownloadList = new ArrayList<>();

    public DownloadRvAdapter(Context context) {
        super();
        this.mContext = context.getApplicationContext();
        this.mDownloadList.add("http://staticlive.douyutv.com/upload/client/douyu_client_1_0v1_8_9.apk");
        this.mDownloadList.add("http://wsdownload.hdslb.net/app/BiliPlayer3.apk");
        this.mDownloadList.add("http://download.virtualbox.org/virtualbox/5.0.8/VirtualBox-5.0.8-103449-OSX.dmg");
    }

    private class DownloadVH extends RecyclerView.ViewHolder {

        private Button btn_start_pause;
        private Button btn_cancel;

        private TextView tv_download_task;
        private ProgressBar proBar_download;
        private TextView tv_download_info;

        DownloadVH(View itemView) {
            super(itemView);
            btn_start_pause = (Button) itemView.findViewById(R.id.btn_start_pause);
            btn_cancel = (Button) itemView.findViewById(R.id.btn_cancel);

            tv_download_task = (TextView) itemView.findViewById(R.id.tv_download_task);
            proBar_download = (ProgressBar) itemView.findViewById(R.id.proBar_download);
            tv_download_info = (TextView) itemView.findViewById(R.id.tv_download_info);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.content_main, parent, false);
        return new DownloadVH(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DownloadVH) {
            ((DownloadVH) holder).tv_download_task.setText("任务 "
                    + (position + 1) + " : " + mDownloadList.get(position));

            final DecimalFormat df = new DecimalFormat("0.00");

            final OkDownloadEnqueueListener listener = new OkDownloadEnqueueListener() {

                @Override
                public void onStart(int id) {
                    ((DownloadVH) holder).btn_start_pause.post(new Runnable() {
                        @Override
                        public void run() {
                            ((DownloadVH) holder).btn_start_pause.setEnabled(true);
                            ((DownloadVH) holder).btn_start_pause.setText("暂停");
                        }
                    });
                    Log.e("OkDownload", "onStart");
                }

                @Override
                public void onProgress(int progress, long cacheSize, long totalSize) {
                    ((DownloadVH) holder).proBar_download.setMax(100);
                    ((DownloadVH) holder).proBar_download.setProgress(progress);

                    if (cacheSize != 0 && totalSize != 0) {
                        final float convertCacheSize = ((float) cacheSize) / 1024 / 1024;
                        final float convertTotalSize = ((float) totalSize) / 1024 / 1024;

                        ((DownloadVH) holder).tv_download_info.post(new Runnable() {
                            @Override
                            public void run() {
                                ((DownloadVH) holder).tv_download_info
                                        .setText("" + df.format(convertCacheSize) + "M" + "/"
                                                + df.format(convertTotalSize) + "M");
                            }
                        });
                    }
                }

                @Override
                public void onRestart() {
                    ((DownloadVH) holder).btn_start_pause.post(new Runnable() {
                        @Override
                        public void run() {
                            ((DownloadVH) holder).btn_start_pause.setEnabled(true);
                            ((DownloadVH) holder).btn_start_pause.setText("暂停");
                        }
                    });
                    Log.e("OkDownload", "onRestart");
                }

                @Override
                public void onPause() {
                    ((DownloadVH) holder).btn_start_pause.post(new Runnable() {
                        @Override
                        public void run() {
                            ((DownloadVH) holder).btn_start_pause.setEnabled(true);
                            ((DownloadVH) holder).btn_start_pause.setText("开始");
                        }
                    });
                    Log.e("OkDownload", "onPause");
                }

                @Override
                public void onCancel() {
                    ((DownloadVH) holder).tv_download_info.post(new Runnable() {
                        @Override
                        public void run() {
                            ((DownloadVH) holder).tv_download_info.setText("");
                            ((DownloadVH) holder).btn_start_pause.setEnabled(true);
                            ((DownloadVH) holder).btn_start_pause.setText("开始");
                            Toast.makeText(mContext, "onCancel", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((DownloadVH) holder).proBar_download.setProgress(0);
                    Log.e("OkDownload", "onCancel");
                }

                @Override
                public void onFinish() {
                    ((DownloadVH) holder).proBar_download.setProgress(100);
                    Log.e("OkDownload", "onFinish");
                }

                @Override
                public void onError(final OkDownloadError error) {
                    ((DownloadVH) holder).proBar_download.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("OkDownload", error.getMessage());
                }
            };

            ((DownloadVH) holder).btn_start_pause.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String filePath = null;
                    File fileDir = mContext.getExternalFilesDir("download");

                    if (hasSDCard() && fileDir != null) {
                        filePath = fileDir.getAbsolutePath() + File.separator + position + ".apk";
                    }

                    OkDownloadRequest request = new OkDownloadRequest.Builder()
                            .url(mDownloadList.get(position))
                            .filePath(filePath)
                            .build();
                    ((DownloadVH) holder).btn_start_pause.setEnabled(false);
                    OkDownloadManager.getInstance(mContext).enqueue(request, listener);
                }
            });

            ((DownloadVH) holder).btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkDownloadManager.getInstance(mContext).onCancel(mDownloadList.get(position), listener);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDownloadList.size();
    }

    private static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
}
