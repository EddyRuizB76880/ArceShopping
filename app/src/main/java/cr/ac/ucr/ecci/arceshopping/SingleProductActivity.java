package cr.ac.ucr.ecci.arceshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.List;

import cr.ac.ucr.ecci.arceshopping.db.DbShoppingCart;
import cr.ac.ucr.ecci.arceshopping.model.Product;

public class SingleProductActivity extends AppCompatActivity {

    private TextView productTitle;
    private TextView productDescription;
    private TextView productCategory;
    private TextView productPrice;
    private TextView textCounter;
    private Product product;
    private ImageSlider carousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product);
        productTitle = (TextView) findViewById(R.id.single_product_title);
        productDescription = (TextView) findViewById(R.id.single_product_description);
        productCategory = (TextView) findViewById(R.id.single_product_category);
        productPrice = (TextView) findViewById(R.id.single_product_price);
        textCounter = (TextView) findViewById(R.id.cart_quantity);
        textCounter.setText("1");
        carousel = (ImageSlider) findViewById(R.id.single_product_image_slider);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.product = getIntent().getExtras().getParcelable("product");
        productTitle.setText(this.product.getTitle());
        productDescription.setText(this.product.getDescription());
        productCategory.setText("Category: " + this.product.getCategory());
        productPrice.setText("$"+Integer.toString(this.product.getPrice()));
        ArrayList<String> productList = new ArrayList<>(this.product.getImages());
        List<SlideModel> slideList = new ArrayList<>();
        for (int index = 0; index < productList.size(); index++) {
            String var = productList.get(index);
            slideList.add(new SlideModel(var,"", ScaleTypes.FIT));
        }
        carousel.setImageList(slideList, ScaleTypes.FIT);
    }

    /**
     * Increases the product counter
     */
    public void addCounter(View view) {
        int counter = Integer.parseInt(textCounter.getText().toString()) + 1;
        if (counter <= 10 && counter <= product.getStock()) {
            textCounter.setText(Integer.toString(counter));
        }
    }

    /**
     * Decreases the product counter
     */
    public void deductCounter(View view) {
        int counter = Integer.parseInt(textCounter.getText().toString()) - 1;
        if (counter >= 1) {
            textCounter.setText(Integer.toString(counter));
        }
    }

    /**
     * Adds the current product to the cart and then goes the CartActivity
     */
    public void addToCart(View view) {
        DbShoppingCart db = new DbShoppingCart(this);
        SharedPreferences sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        db.insertProduct(sp.getString("userEmail",""), this.product.getId(), Integer.parseInt(textCounter.getText().toString()), this.product.getPrice());
        Toast.makeText(this, "Producto añadido éxitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}