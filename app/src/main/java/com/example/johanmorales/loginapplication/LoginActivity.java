package com.example.johanmorales.loginapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.johanmorales.loginapplication.Models.Employee;
import com.example.johanmorales.loginapplication.Models.Respuesta;
import com.example.johanmorales.loginapplication.Models.Resultado;
import com.example.johanmorales.loginapplication.utils.Md5Manager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 *
 *  implements LoaderCallbacks<Cursor>
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String REGION = "America/Bogota";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    public String user;
    public String password;
    public String region = REGION;

    public Respuesta respuesta;
    private Resultado resultado;
    private Employee empleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login - SAI");

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                // Reset errors.
                mEmailView.setError(null);
                mPasswordView.setError(null);

                user = mEmailView.getText().toString();
                String pass = mPasswordView.getText().toString();

                //boolean cancel = false;
                View focusView = null;

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(pass)) {

                    mPasswordView.setError(getString(R.string.error_invalid_password));

                    focusView = mPasswordView;
                    focusView.requestFocus();

                }else if(TextUtils.isEmpty(user)){

                    mEmailView.setError(getString(R.string.error_invalid_email));

                    focusView = mEmailView;
                    focusView.requestFocus();

                }else{

                    password = Md5Manager.encode(pass);

                    login(user, password, region);
                }


            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }

    //validar que no deba iniciar sesion nuevamente si ya existen las credenciales
    @Override
    protected void onResume() {
        super.onResume();

        if(respuesta != null){
            Log.d(TAG, "Ya existe la respuesta, debe iniciar main activity");
            initMainActivity();
        }else{
            Log.d(TAG, "No existe la respuesta");
        }
    }

    private void login(String user, String password, String region){

        //Log.d(TAG,"Se pasaron los parámetros user: "+user+" y pass: "+password);

        mProgressView.setVisibility(View.VISIBLE);
        mLoginFormView.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(this);

        String urlApi = "https://sai-auth.azurewebsites.net/api/authentication";

        JSONObject req = new JSONObject();

        try {
            req.put("username",user);
            req.put("password",password);
            req.put("region",region);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, urlApi, req, new Response.Listener<JSONObject>() {

                    //cuOfJs

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(final JSONObject response) {

                        //executeResponseJsonApp(response);

                        JSONObject res = response;

                        Log.d(TAG,"Status de la respuesta: "+res.toString());

                        respuesta = new Respuesta();
                        resultado = new Resultado();
                        empleado = new Employee();

                        try {

                            JSONObject result = (JSONObject) res.get("result");
                            JSONObject employee = (JSONObject) result.get("employee");

                            respuesta.setSucces(res.getBoolean("success"));
                            respuesta.setMessage(res.getString("message"));

                            //---------------------------------------------------

                            empleado.setFirstName(employee.getString("firstName"));
                            empleado.setLastName(employee.getString("lastName"));
                            empleado.setPosition(employee.getString("position"));
                            empleado.setRegion(employee.getString("region"));
                            //---------------------------------------------------

                            resultado.setToken(result.getString("token"));
                            resultado.setEmployee(empleado);

                            respuesta.setResult(resultado);

                            Log.d(TAG, "El mensaje de la respuesta es: "+respuesta.getMessage());

                            Log.d(TAG,"token: "+resultado.getToken());
                            //----------------------------------------------------
                            /**
                             *  Intent hourlyActivityIntent = new Intent(MainActivity.this,HourlyForecastActivity.class);
                             *
                             *  //en este intent se añade un extra llamado socialNumber con el valor de tipo int
                             *  hourlyActivityIntent.putExtra("SocialNumber",1024524163);
                             *
                             *  hourlyActivityIntent.putParcelableArrayListExtra("hours",arrListHours);
                             *
                             *  startActivity(hourlyActivityIntent);
                             * */

                            initMainActivity();
                            //----------------------------------------------------

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                        Log.d(TAG, error.toString());

                        mProgressView.setVisibility(View.GONE);
                        mLoginFormView.setVisibility(View.VISIBLE);

                        //despliegue del error en un toast
                        Toast.makeText(LoginActivity.this,getString(R.string.error_response)+": "+error.toString(),Toast.LENGTH_LONG).show();

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void initMainActivity(){

        Intent mainActivityIntent = new Intent(LoginActivity.this, Main2Activity.class);

        mainActivityIntent.putExtra("respuesta", respuesta);

        //mainActivityIntent.putExtra("resultado", resultado);

        //mainActivityIntent.putExtra("empleado", empleado);

        startActivity(mainActivityIntent);
    }

    @Override
    public void onBackPressed() {

        if (respuesta == null) {
            Log.d(TAG, "se debe generar la respuesta nuevamente.");
        } else {
            super.onBackPressed();
        }
    }

    /*

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    */
}

