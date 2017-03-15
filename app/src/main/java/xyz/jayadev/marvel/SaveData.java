package xyz.jayadev.marvel;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class SaveData extends AsyncTask<String, Void, String> {
    Bitmap b;
    String id;

    public interface TaskListener {
        void onFinished(String result);
    }

    private final TaskListener taskListener;

    SaveData(TaskListener listener, Bitmap b, String id) {
        this.taskListener = listener;
        this.b = b;
        this.id = id;

    }

    @Override
    protected String doInBackground(String... params) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/marvel");
        myDir.mkdirs();
        String fname = this.id + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            this.b.compress(Bitmap.CompressFormat.JPEG, 0, out);
            out.flush();
            out.close();
            return root + "/marvel/" + this.id + ".jpg";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(String result) {
        this.taskListener.onFinished(result);
    }
}
