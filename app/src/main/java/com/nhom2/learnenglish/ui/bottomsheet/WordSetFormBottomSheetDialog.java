package com.nhom2.learnenglish.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class WordSetFormBottomSheetDialog extends BottomSheetDialogFragment {

    public interface Listener {
        void onWordSetSaved(String name, String description, String iconCategory, @Nullable Long existingId);
    }

    private Listener listener;
    private String selectedCategory = "folder";
    private ImageButton[] categoryButtons;

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
        return inflater.inflate(R.layout.bottom_sheet_word_set_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        Long existingId = args != null && args.containsKey(ARG_ID) ? args.getLong(ARG_ID) : null;
        if (existingId != null && existingId <= 0) {
            existingId = null;
        }
        String initialName = args != null ? args.getString(ARG_NAME, "") : "";
        String initialDesc = args != null ? args.getString(ARG_DESC, "") : "";
        String initialCategory = args != null ? args.getString(ARG_CATEGORY, "folder") : "folder";

        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        TextInputLayout tilName = view.findViewById(R.id.til_set_name);
        TextInputEditText etName = view.findViewById(R.id.et_set_name);
        MaterialButton btnSave = view.findViewById(R.id.btn_save);

        tvTitle.setText(existingId == null ? R.string.word_set_add_title : R.string.word_set_edit_title);
        btnSave.setText(existingId == null ? R.string.action_create_set : R.string.action_save);

        if (!initialName.isEmpty()) {
            etName.setText(initialName);
        }

        setupCategories(view, initialCategory);

        Long finalExistingId = existingId;
        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            if (name.isEmpty()) {
                tilName.setError(getString(R.string.error_name_required));
                return;
            }
            tilName.setError(null);
            if (listener != null) {
                listener.onWordSetSaved(name, null, selectedCategory, finalExistingId);
            }
            dismiss();
        });
    }

    private void setupCategories(View view, String initialCategory) {
        categoryButtons = new ImageButton[]{
                view.findViewById(R.id.cat_travel),
                view.findViewById(R.id.cat_food),
                view.findViewById(R.id.cat_study),
                view.findViewById(R.id.cat_business),
                view.findViewById(R.id.cat_tech)
        };
        String[] categories = {"travel", "food", "study", "business", "laptop"};

        for (int i = 0; i < categoryButtons.length; i++) {
            final String category = categories[i];
            categoryButtons[i].setOnClickListener(v -> selectCategory(category));
        }
        selectCategory(initialCategory.isEmpty() ? "folder" : initialCategory);
    }

    private void selectCategory(String category) {
        selectedCategory = category;
        String[] categories = {"travel", "food", "study", "business", "laptop"};
        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i].setSelected(category.equals(categories[i]));
        }
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

    public static WordSetFormBottomSheetDialog newInstanceForAdd() {
        return new WordSetFormBottomSheetDialog();
    }

    public static WordSetFormBottomSheetDialog newInstanceForEdit(
            long id, String name, String description, String iconCategory) {
        WordSetFormBottomSheetDialog dialog = new WordSetFormBottomSheetDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESC, description != null ? description : "");
        args.putString(ARG_CATEGORY, iconCategory != null ? iconCategory : "folder");
        dialog.setArguments(args);
        return dialog;
    }

    private static final String ARG_ID = "id";
    private static final String ARG_NAME = "name";
    private static final String ARG_DESC = "desc";
    private static final String ARG_CATEGORY = "category";
}
