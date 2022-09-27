package cr.ac.ucr.ecci.arceshopping.api;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import cr.ac.ucr.ecci.arceshopping.Product;
import cr.ac.ucr.ecci.arceshopping.Products;
import cr.ac.ucr.ecci.arceshopping.R;
import cr.ac.ucr.ecci.arceshopping.databinding.FragmentApiBinding;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

//import com.example.communication.ListaQuotes;
//import com.example.communication.Quote;
//import com.example.communication.R;

import com.google.gson.*;

import android.widget.ListView;

import java.util.List;



public class ApiFragment extends Fragment {

    private FragmentApiBinding binding;
    private final String URLEXAMPLE = "https://dummyjson.com/products";

    public Products productos;

    private ListView listQuotes;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentApiBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText theFilter = (EditText) root.findViewById(R.id.searchFilter);
        //log

        StringRequest myRequest = new StringRequest(Request.Method.GET,
                URLEXAMPLE,
                response -> {
                    try{
                        JSONObject myJsonObject = new JSONObject(response);
                        String items = myJsonObject.toString();
                        Gson gson = new Gson();

                        Products products = gson.fromJson(items, Products.class);
                        listQuotes = root.findViewById(R.id.list);

                        ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(root.getContext(), android.R.layout.simple_list_item_1,
                                products.getProducts());
                        listQuotes.setAdapter(adapter);

                        productos = products;
                        theFilter.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                adapter.getFilter().filter(charSequence);
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> Toast.makeText(getActivity(),
                        volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(myRequest);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}