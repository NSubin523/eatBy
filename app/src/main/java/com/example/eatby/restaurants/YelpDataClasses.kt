package com.example.eatby.restaurants

data class YelpSearchResult(
     val total: Int,
     val businesses: List<YelpRestaurant>
)

data class YelpRestaurant(
    val id : String,
    val name : String,
    val rating : Double,
    val price : String,
    val phone: String,
    val url: String,
    val is_closed: Boolean,
    val review_count: Int,
    val distance: Double,
    val image_url : String,
    val categories: List<YelpCategory>,
    val location: YelpLocation
) {
    fun displayDistance(): String{
        val miles = 0.000621371
        val distanceInMiles = "%.2f".format(distance*miles)
        return "$distanceInMiles mi"
    }
}

data class YelpCategory(
    val title : String
)

data class YelpLocation(
    val address1: String
)