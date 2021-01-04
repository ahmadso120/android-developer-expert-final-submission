package com.sopian.imageapp.core.ui

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sopian.imageapp.core.R
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat


@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    Picasso.get()
        .load(imageUrl)
        .placeholder(R.drawable.ic_loading)
        .error(R.drawable.ic_loading)
        .into(view)
}

@BindingAdapter("isFavorite")
fun bindIsFavorite(view: FloatingActionButton, status: Boolean?) {
    status?.let {
        val iconResource = if (status) {
            R.drawable.ic_favorite_white
        } else {
            R.drawable.ic_not_favorite_white
        }
        view.setImageResource(iconResource)
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("formattedText")
fun formattedText(view: TextView, date: String?) {
    if (date != null){
        val simpleDateFormat = SimpleDateFormat("dd, MMM yyyy")
        val simpleDateParseFormat = SimpleDateFormat("yyyy-MM-dd")
        try {
            val fdd = simpleDateFormat.format(
                simpleDateParseFormat.parse(date))
            view.text = fdd
        } catch (e: ParseException) {
            Timber.e(e.message.toString())
        }
    }
}

@BindingAdapter("appendDashIfNUll")
fun appendDashIfNUll(view: TextView, text: String?){
    if (text == null || text.contains("null")) view.text = "--" else view.text = text
}

@BindingAdapter("formattedNumber")
fun formattedNumber(view: TextView, number: Int?){
    val formatter: NumberFormat = DecimalFormat("#,###")

    val formattedNumber: String = formatter.format(number ?: 0)
    view.text = if (number == null) "--" else formattedNumber
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}