package com.epf.min.scanfood

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_results_product.*
import okhttp3.*
import java.io.IOException


class ResultsProduct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_product)


        var number = intent.getStringExtra("number")
        fetchJson(number)

    }

/* Parsing du fichier json récupéré par url*/

    fun fetchJson(number : String?) {
        println("Tentative de récupération JSON")

        val url = "https://world.openfoodfacts.org/api/v0/product/$number"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
              println("Echec de la requête")
            }

            override fun onResponse(call: Call, response: Response) {
               val body = response?.body?.string()
                println(body)
                val gson =GsonBuilder().create()

                val products = gson.fromJson(body, Products::class.java)
                viewDisplay(products)



            }

             fun viewDisplay(products: Products){
                 val name = products.product.generic_name
                 name_textview.text = name

                 val brand = products.product.brands
                 brand_textView.text = brand

                 val categories = products.product.categories
                 categories_textView.text = categories

                 val ingredients = products.product.ingredients

                 var test = ""

                 for (ingredient in ingredients){

                     test+= ingredient.text + ", "
                 }

                 ingredients_TextView.text = test

                 val nutrition = products.product.nutriments.`nutrition-score-fr_100g`
                 var nutritionscore: String = ""

                 when(nutrition) {
                     0 -> nutritionscore = "E"
                     1 -> nutritionscore = "E"
                     2 -> nutritionscore = "D"
                     3 -> nutritionscore = "C"
                     4 -> nutritionscore = "B"
                     5 -> nutritionscore = "A"
                 }

                 nutriscore_textView.text = nutritionscore

/*morceau de code s'occuper de la récupération de l'image à partir de l'url du json avec la librairie Glide, cette partie provoque
un plantage de l'application après le scan. La photo ne s'affiche pas*/

               /* val image = products.product.image_url

                 Glide.with(this@ResultsProduct).asBitmap().load(image).placeholder(R.drawable.loading_image).transform(CircleCrop()).into(
                     BitmapImageViewTarget(image_imageView)
                 )*/
             }

        })
    }



}
class Products(val product: Product)
class Product(val brands: String, val generic_name: String,
              val categories: String,
              val nutriments : Nutriment,
              val ingredients: MutableList<Ingredient>,
              val image_url: String)
class Ingredient(val text: String)
class Nutriment( val `nutrition-score-fr_100g`: Number)


