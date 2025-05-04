package im.mingxi.loader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpUtil {
    public static String sendDataRequest(String url) {

        final StringBuffer buffer = new StringBuffer();
        Thread mThread =
                new Thread(
                        new Runnable() {

                            public void run() {
                                InputStreamReader isr = null;
                                try {
                                    URL urlObj = new URL(url);
                                    URLConnection uc = urlObj.openConnection();
                                    uc.setConnectTimeout(3000);
                                    uc.setReadTimeout(3000);
                                    isr = new InputStreamReader(uc.getInputStream(), "utf-8");
                                    BufferedReader reader = new BufferedReader(isr);
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        buffer.append(line + "\n");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (null != isr) {
                                            isr.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (buffer.length() == 0) return buffer.toString();
        buffer.delete(buffer.length() - 1, buffer.length());
        return buffer.toString();
    }
    public static boolean downloadToFile(String url, String local) {
    try {
      if (Thread.currentThread().getName().equals("main")) {
        AtomicBoolean builder = new AtomicBoolean();
        Thread thread = new Thread(() -> builder.getAndSet(downloadToFile(url, local)));
        thread.start();
        thread.join();
        return builder.get();
      }
      File cache = new File(PathUtil.appPath + "cache");
      if (!cache.exists()) cache.mkdirs();
      String cachePath = PathUtil.appPath + "cache/" + Math.random();
      File parent = new File(local).getParentFile();
      if (!parent.exists()) parent.mkdirs();

      FileOutputStream fOut = new FileOutputStream(cachePath);
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(10000);
      connection.setReadTimeout(10000);
      InputStream ins = connection.getInputStream();
      byte[] buffer = new byte[4096];
      int read;
      while ((read = ins.read(buffer)) != -1) {
        fOut.write(buffer, 0, read);

        // 线程中断
        if (Thread.currentThread().isInterrupted()) {
          fOut.close();
          ins.close();
          return false;
        }
      }
      fOut.flush();
      fOut.close();
      ins.close();

      if (new File(cachePath).length() < 1) return false;
      FileUtil.copyFile(cachePath, local);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
