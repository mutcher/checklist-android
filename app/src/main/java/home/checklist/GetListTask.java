package home.checklist;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */
public class GetListTask extends AsyncTask<Integer, Void, ArrayList<String>> {
    @Override
    protected ArrayList<String> doInBackground(Integer... integers) {
        serverConnector connector = ServerContainer.getConnector();
        byte listId = integers[0].byteValue();

        ConnectionHelper helper = new ConnectionHelper(connector);
        try {
            return helper.loadList(listId);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
