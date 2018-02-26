package farijo.com.starcraft_bo_shower.network.bo_grabber;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by Eikichi on 14/02/2018.
 */

public class BOGrabber {

    private final Context context;

    public BOGrabber(Context context) {
        this.context = context;
    }

    public void grab(String url) {
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(buildRequestFor(url));
        queue.start();
    }

    private StringRequest buildRequestFor(String url) {
        return new StringRequest(Request.Method.GET, url, new BOResponseListener(), new BOErrorListener());
    }
}
