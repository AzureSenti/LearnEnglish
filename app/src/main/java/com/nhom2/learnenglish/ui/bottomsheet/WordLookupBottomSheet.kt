package com.nhom2.learnenglish.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.nhom2.learnenglish.R
import com.nhom2.learnenglish.core.data.local.entity.WordEntity

class WordLookupBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_word_lookup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tappedWord = arguments?.getString(ARG_WORD).orEmpty()
        val meaning = arguments?.getString(ARG_MEANING)
        val inDictionary = arguments?.getBoolean(ARG_IN_DICTIONARY, false) == true

        view.findViewById<TextView>(R.id.tv_word).text = tappedWord

        val tvMeaning = view.findViewById<TextView>(R.id.tv_meaning)
        val tvStatus = view.findViewById<TextView>(R.id.tv_status)

        if (inDictionary && !meaning.isNullOrBlank()) {
            tvMeaning.text = meaning
            tvStatus.visibility = View.VISIBLE
            tvStatus.setText(R.string.word_in_dictionary)
            tvStatus.setTextColor(requireContext().getColor(R.color.green_tag_text))
        } else {
            tvMeaning.text = getString(R.string.word_not_in_dictionary)
            tvStatus.visibility = View.GONE
        }

        view.findViewById<MaterialButton>(R.id.btn_close).setOnClickListener { dismiss() }
    }

    companion object {
        private const val ARG_WORD = "word"
        private const val ARG_MEANING = "meaning"
        private const val ARG_IN_DICTIONARY = "in_dictionary"

        @JvmStatic
        fun newInstance(word: String, entity: WordEntity?): WordLookupBottomSheet {
            return WordLookupBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_WORD, word)
                    putBoolean(ARG_IN_DICTIONARY, entity != null)
                    putString(ARG_MEANING, entity?.vietnameseMeaning)
                }
            }
        }
    }
}
