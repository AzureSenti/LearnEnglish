package com.nhom2.learnenglish.feature.articles

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nhom2.learnenglish.R
import com.nhom2.learnenglish.core.data.local.entity.WordEntity
import java.util.regex.Pattern

/**
 * Tách nội dung bài báo thành các token từ tiếng Anh và gắn [ClickableSpan]
 * để mỗi từ có thể bấm và mở Bottom Sheet tra nghĩa.
 */
object ArticleTextSpanHelper {

    private val WORD_PATTERN: Pattern = Pattern.compile("\\b[\\p{L}]+(?:'[\\p{L}]+)?\\b")

    fun applyToTextView(
        textView: TextView,
        content: String,
        vocabularyMap: Map<String, WordEntity>,
        highlightKnownWords: Boolean = true,
        onWordClick: (String, WordEntity?) -> Unit
    ) {
        val spannable = buildClickableSpannable(
            content = content,
            vocabularyMap = vocabularyMap,
            highlightKnownWords = highlightKnownWords,
            knownWordColor = ContextCompat.getColor(textView.context, R.color.blue_primary),
            onWordClick = onWordClick
        )
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }

    fun buildClickableSpannable(
        content: String,
        vocabularyMap: Map<String, WordEntity>,
        highlightKnownWords: Boolean = true,
        knownWordColor: Int,
        onWordClick: (String, WordEntity?) -> Unit
    ): SpannableString {
        val spannable = SpannableString(content)
        val matcher = WORD_PATTERN.matcher(content)

        while (matcher.find()) {
            val word = matcher.group() ?: continue
            val start = matcher.start()
            val end = matcher.end()
            val entity = vocabularyMap[word.lowercase()]

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: android.view.View) {
                    onWordClick(word, entity)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                    if (highlightKnownWords && entity != null) {
                        ds.color = knownWordColor
                        ds.isFakeBoldText = true
                    }
                }
            }

            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (highlightKnownWords && entity != null) {
                spannable.setSpan(
                    ForegroundColorSpan(knownWordColor),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannable
    }
}
