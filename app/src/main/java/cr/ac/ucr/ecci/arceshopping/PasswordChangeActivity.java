package cr.ac.ucr.ecci.arceshopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cr.ac.ucr.ecci.arceshopping.db.DbUsers;

public class PasswordChangeActivity extends ConnectedActivity {

    private TextInputLayout newPassword;
    private TextInputLayout confirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        newPassword = (TextInputLayout) findViewById(R.id.Password_new);
        confirmPassword = (TextInputLayout) findViewById(R.id.Password_confirm);
        sp = getSharedPreferences("login",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Change the user's password
     */
    public void changePassword(View view) {
        String theNewPassword = newPassword.getEditText().getText().toString();
        String theConfirmPassword = confirmPassword.getEditText().getText().toString();
        boolean validNewPassword = isValidNewPassword(theNewPassword);
        boolean validConfirmPassword = isValidNewPassword(theConfirmPassword);
        FirebaseUser user = mAuth.getCurrentUser();
        if (theNewPassword.equals(theConfirmPassword) && validNewPassword && validConfirmPassword) {
            user.updatePassword(theNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(PasswordChangeActivity.this, "Cambio de contraseña exitoso",
                                Toast.LENGTH_LONG).show();
                        if (changeInDb(user.getUid())) {
                            sp.edit().putString("userPassword", theNewPassword).apply();
                            Intent intent = new Intent(PasswordChangeActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else
                    {
                        Toast.makeText(PasswordChangeActivity.this, "Hubo un error",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Change the user's password in Cloud FireStone from fireBase
     * @param userId The id to identify the current user
     * @return true if the password was changed in FireBase, false otherwise
     */
    private boolean changeInDb(String userId)
    {
        final boolean[] success = {true};
        db.collection("User").document(userId).update("passwordIsChanged", true).
                addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful())
                        {
                            System.out.println(task.getException().toString());
                            Toast.makeText(PasswordChangeActivity.this, "Hubo un error, " +
                                    "intente otra vez", Toast.LENGTH_LONG).show();
                            success[0] = false;
                        }
                    }
                }
        );
        return success[0];
    }

    /**
     * Checks if the new password inserted by the user is valid (the password must be at least 6
     * characters long)
     * @param password The password that has to be checked
     * @return True if the password is valid, false otherwise
     */
    private boolean isValidNewPassword(String password) {
        if (password.length() == 0) {
            newPassword.setError("Escriba su contraseña");
            return false;
        }
        if (password.length() < 6) {
            confirmPassword.setError("La contraseña debe estar compuesto de al menos 6 caracteres");
            return false;
        }
        newPassword.setError(null);
        return true;
    }
}