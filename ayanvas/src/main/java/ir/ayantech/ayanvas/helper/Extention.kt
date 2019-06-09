package ir.ayantech.ayanvas.helper

import android.graphics.BitmapFactory
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Base64
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import java.io.ByteArrayInputStream

fun ImageView.loadBase64(base64Image: String) {
    val stream = ByteArrayInputStream(
        Base64.decode(
            base64Image, Base64.DEFAULT
        )
    )
    this.setImageBitmap(BitmapFactory.decodeStream(stream))
}

fun EditText.setOnTextChange(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            listener(p0?.toString() ?: "")
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}

fun TextView.setHtmlText(html: String?) {
    text = if (!(html?.contains("<html>") == true && html.contains("</html>"))) {
        html
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
    }
}