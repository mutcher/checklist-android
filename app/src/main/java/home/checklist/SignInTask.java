package home.checklist;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by Gorbatiuk Illia on 05.04.2016.
 */
public class SignInTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        serverConnector connector = ServerContainer.getConnector();
        ConnectionHelper helper = new ConnectionHelper(connector);
        try {
            return helper.signIn(strings[0], strings[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
