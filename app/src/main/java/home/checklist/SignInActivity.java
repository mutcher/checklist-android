package home.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void onSignIn(View v)
    {
        EditText loginEdit = (EditText)findViewById(R.id.loginEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);

        String login = loginEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        SignInTask task = new SignInTask();
        task.execute(login, password);
        try {
            if (task.get(2, TimeUnit.SECONDS)) {
                Intent intent = new Intent(getBaseContext(), MainListActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Incorrect login or password", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
