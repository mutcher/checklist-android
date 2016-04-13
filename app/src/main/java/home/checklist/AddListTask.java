package home.checklist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */
public class AddListTask extends AsyncTask<String, Void, Void> {
    private byte _listID = 0;
    public AddListTask(byte listID) {
        _listID = listID;
    }

    @Override
    protected Void doInBackground(String... strings) {
        serverConnector connector = ServerContainer.getConnector();
        ConnectionHelper helper = new ConnectionHelper(connector);

        try {
            if (!helper.addListItem(_listID, strings[0])) {
                Log.i("AddListTask", "Not added");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
