package home.checklist;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Gorbatiuk Illia on 09.04.2016.
 */
public class RemoveListTask extends AsyncTask<String, Void, Boolean> {

    byte _listID = 0;

    void setListId(int listID) {
        _listID = (byte)listID;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String listName = strings[0];
        serverConnector connector = ServerContainer.getConnector();
        ConnectionHelper helper = new ConnectionHelper(connector);
        try {
            return helper.removeList(_listID, listName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
