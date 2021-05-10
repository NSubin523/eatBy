package com.example.eatby.restaurants

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.eatby.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_restaurant.view.*

class RestaurantsAdapter(val context: Context, val restaurants: List<YelpRestaurant>) :
    RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant,parent,false))
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.bind(restaurant)
    }

    override fun getItemCount() = restaurants.size

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        @ExperimentalStdlibApi
        fun bind(restaurant: YelpRestaurant) {
            itemView.tvName.text = restaurant.name
            itemView.ratingBar.rating = restaurant.rating.toFloat()
            itemView.tvNumReviwes.text = "${restaurant.review_count} reviews"
            itemView.tvAddress.text = restaurant.location.address1
            itemView.tvCategory.text = restaurant.categories[0].title
            itemView.tvDistance.text = restaurant.displayDistance()
            itemView.tvPrice.text = restaurant.price
            if(!restaurant.is_closed) itemView.openClosed.text = "Open"
            else itemView.openClosed.text = "Closed"
            Glide.with(context).load(restaurant.image_url).apply(RequestOptions().transforms(
                CenterCrop(), RoundedCorners(20)
            )).into(itemView.imageView)
            itemView.getDirection.setOnClickListener{
                val u = Uri.parse("http://maps.google.co.in/maps?q=" + restaurant.name)
                val intent = Intent(Intent.ACTION_VIEW,u)
                itemView.context.startActivity(intent)
            }
                itemView.callPhone.setOnClickListener{
                    val dialIntent = Intent(Intent.ACTION_DIAL)
                    dialIntent.data = Uri.parse("tel:" + restaurant.phone)
                    itemView.context.startActivity(dialIntent)
            }
            itemView.getMenu.setOnClickListener{
                val re = Regex("[^A-Za-z]")
                var rName = restaurant.name
                rName = re.replace(rName,"").lowercase()
                val url = "https://$rName.com"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                itemView.context.startActivity(intent)
            }
            itemView.writeReview.setOnClickListener{
                val intent = Intent(context,Reviews::class.java)
                intent.putExtra("title",restaurant.name)
                intent.putExtra("imageUrl",restaurant.image_url)
                itemView.context.startActivity(intent)
            }
            itemView.addToFavorites.setOnClickListener{
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Add to Favorites").setMessage("Add "+restaurant.name+" to favorites?")
                    .setNegativeButton("No",null).setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                        val db = FirebaseFirestore.getInstance()
                        val fAuth = FirebaseAuth.getInstance()
                        val userId = fAuth.currentUser.uid
                        val dRef = db.collection("userFavorites").document(userId).collection("restaurantList")
                        val userFav : MutableMap<String,Any> = HashMap()
                        userFav ["name"] = restaurant.name
                        userFav ["imageUrl"] = restaurant.image_url
                        userFav["rating"] = restaurant.rating
                        userFav["address"] = restaurant.location.address1
                        userFav["category"] = restaurant.categories[0].title
                        userFav["price"] = restaurant.price
                        userFav["distance"] = restaurant.displayDistance()
                        userFav["phoneNumber"] = restaurant.phone

                        dRef.add(userFav).addOnSuccessListener {
                            Toast.makeText(context,"Added to favorites",Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener{
                                Toast.makeText(context,"cannot connect to database error", Toast.LENGTH_SHORT).show()
                            }
                    }
                val alert = builder.create()
                alert.show()
            }
        }

    }
}
