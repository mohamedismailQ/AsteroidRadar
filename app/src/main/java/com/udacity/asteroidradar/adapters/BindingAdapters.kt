package com.udacity.asteroidradar

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.udacity.asteroidradar.adapters.AsteroidAdapter
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.viewModel.AsteroidApiStatus
import com.udacity.asteroidradar.network.PictureOfDay

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = imageView.resources.getString(
            R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = imageView.resources.getString(
            R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("pictureOfDay")
fun bindPicOfDayImage(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    if (pictureOfDay != null && pictureOfDay.mediaType.equals("image")) {
        val imgUri = pictureOfDay.url?.toUri()?.buildUpon()?.scheme("https")?.build()
        Glide.with(imageView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imageView)
        imageView.contentDescription = String.format(imageView.resources.getString(
            R.string.nasa_picture_of_day_content_description_format), pictureOfDay.title)
    } else if (pictureOfDay != null) { // This is to avoid getting Image not found before loading the actual image and to cater for video
        Glide.with(imageView.context)
            .load(R.drawable.broken)
            .into(imageView)
    }
}

@BindingAdapter("asteroidStatus")
fun bindAsteroidStatus(progressBar: ProgressBar, statusAsteroid: AsteroidApiStatus?) {
    when (statusAsteroid) {
        AsteroidApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        else -> {
            progressBar.visibility = View.GONE
        }
    }
}


