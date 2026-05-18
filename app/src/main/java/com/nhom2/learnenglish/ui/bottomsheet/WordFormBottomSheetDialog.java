package com.nhom2.learnenglish.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom2.learnenglish.R;

public class WordFormBottomSheetDialog extends BottomSheetDialogFragment {

    public interface Listener {
        void onWordSaved(String englishWord, String vietnameseMeaning, @Nullable Long wordId);
    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getTheme() {
        return R.style.Widget_Sanctuary_BottomSheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_word_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        Long wordId = args != null && args.containsKey(ARG_WORD_ID) ? args.getLong(ARG_WORD_ID) : null;
        if (wordId != null && wordId <= 0) {
            wordId = null;
        }
        String english = args != null ? args.getString(ARG_ENGLISH, "") : "";
        String vietnamese = args != null ? args.getString(ARG_VIETNAMESE, "") : "";

        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        TextInputLayout tilEnglish = view.findViewById(R.id.til_english);
        TextInputLayout tilVietnamese = view.findViewById(R.id.til_vietnamese);
        TextInputEditText etEnglish = view.findViewById(R.id.et_english);
        TextInputEditText etVietnamese = view.findViewById(R.id.et_vietnamese);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        tvTitle.setText(wordId == null ? R.string.word_add_title : R.string.word_edit_title);
        btnSave.setText(wordId == null ? R.string.action_add_to_set : R.string.action_save);

        if (!english.isEmpty()) {
            etEnglish.setText(english);
        }
        if (!vietnamese.isEmpty()) {
            etVietnamese.setText(vietnamese);
        }

        Long finalWordId = wordId;
        btnSave.setOnClickListener(v -> {
            String en = etEnglish.getText() != null ? etEnglish.getText().toString().trim() : "";
            String vi = etVietnamese.getText() != null ? etVietnamese.getText().toString().trim() : "";
            boolean valid = true;
            if (en.isEmpty()) {
                tilEnglish.setError(getString(R.string.error_word_fields_required));
                valid = false;
            } else {
                tilEnglish.setError(null);
            }
            if (vi.isEmpty()) {
                tilVietnamese.setError(getString(R.string.error_word_fields_required));
                valid = false;
            } else {
                tilVietnamese.setError(null);
            }
            if (!valid) {
                return;
            }
            if (listener != null) {
                listener.onWordSaved(en, vi, finalWordId);
            }
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() instanceof BottomSheetDialog) {
            View sheet = ((BottomSheetDialog) getDialog())
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public static WordFormBottomSheetDialog newInstanceForAdd() {
        return new WordFormBottomSheetDialog();
    }

    public static WordFormBottomSheetDialog newInstanceForEdit(long wordId, String english, String vietnamese) {
        WordFormBottomSheetDialog dialog = new WordFormBottomSheetDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_WORD_ID, wordId);
        args.putString(ARG_ENGLISH, english);
        args.putString(ARG_VIETNAMESE, vietnamese);
        dialog.setArguments(args);
        return dialog;
    }

    private static final String ARG_WORD_ID = "word_id";
    private static final String ARG_ENGLISH = "english";
    private static final String ARG_VIETNAMESE = "vietnamese";
}
