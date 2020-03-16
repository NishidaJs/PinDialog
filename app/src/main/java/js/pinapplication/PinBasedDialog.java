package js.pinapplication;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nishida Kai on 16/3/2020.
 */
public class PinBasedDialog extends DialogFragment {

    protected EditText[] chars;
    protected RecyclerView keypad;

    protected Resources resource;
    protected View dialogView;
    private int flag = 0;

    private static final String BUTTON_CLEAR = "clear";
    private static final String BUTTON_SEND = "send";

    @StyleRes
    protected int baseStyle = android.R.style.Theme_DeviceDefault_Dialog_NoActionBar_MinWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Builder builder = new Builder(getContext(), baseStyle);
        builder = configComponents(builder);
        dialogView = View.inflate(getContext(), getViewRes(), null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreateViewDone();
        configSize();
        return dialogView;
    }

    @Override
    public void onStart() {
        if(dialogView != null && flag == 0) {
            try {
                getDialog().setContentView(dialogView);
                flag++;
            } catch (IllegalStateException ise) {
                ise.printStackTrace();
            }
        }
        super.onStart();
    }

    @Override
    public final void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if(okButton != null) {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPositiveButtonClicked();
                }
            });
        }
        Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if(cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNegativeButtonClicked();
                }
            });
        }
    }

    @LayoutRes
    protected int getViewRes() {
        return R.layout.dialog_pin_setup;
    }

    protected Builder configComponents(Builder b) {
        b.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });
        b.setCancelable(false);
        return b;
    }

    protected void onCreateViewDone() {
        getDialog().setCanceledOnTouchOutside(false);
        init();
    }

    private void init() {
        TextView tooltip = dialogView.findViewById(R.id.tooltip);
        tooltip.setText("Please enter your 6-digit PIN to proceed.");
        chars = new EditText[] {dialogView.findViewById(R.id.input1), dialogView.findViewById(R.id.input2),
                dialogView.findViewById(R.id.input3), dialogView.findViewById(R.id.input4),
                dialogView.findViewById(R.id.input5), dialogView.findViewById(R.id.input6)};

        keypad = dialogView.findViewById(R.id.inputKeypad);
        GridLayoutManager glm = new GridLayoutManager(getContext(), 3);
        keypad.setLayoutManager(glm);
        PinAdapter adp = new PinAdapter();
        keypad.setAdapter(adp);
    }

    protected void configSize() {

    }

    protected void onPositiveButtonClicked() {

    }

    protected void onNegativeButtonClicked() {

    }

    protected void onBackPressed() {
        android.app.AlertDialog dlg = new Builder(getContext()).create();
        dlg.setTitle("Confirmation");
        dlg.setMessage("Do you want to cancel this operation?");
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        dlg.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (DialogInterface.OnClickListener) null);
        dlg.show();
    }


    public class PinAdapter extends RecyclerView.Adapter<VH> {
        private List<PadKey> list;
        public PinAdapter() {
            this.list = new ArrayList<>();
            list.add(new PadKey("1", "1", false));
            list.add(new PadKey("2", "2", false));
            list.add(new PadKey("3", "3", false));
            list.add(new PadKey("4", "4", false));
            list.add(new PadKey("5", "5", false));
            list.add(new PadKey("6", "6", false));
            list.add(new PadKey("7", "7", false));
            list.add(new PadKey("8", "8", false));
            list.add(new PadKey("9", "9", false));
            list.add(getLeftPadKey());
            list.add(new PadKey("0", "0", false));
            list.add(getRightPadKey());
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rlist_number_pad, viewGroup, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH vh, int i) {
            final PadKey pk = list.get(i);
            Button button = vh.getButton();
            button.setText(pk.padValue);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pk.isFunctional) {
                        onFunctionButtonClicked(pk.padValue, pk.keyValue);
                    } else {
                        String character = ((Button) v).getText().toString();
                        appendOneChar(character);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return 12;
        }
    }

    protected PadKey getLeftPadKey() {
        return new PadKey("Clear", BUTTON_CLEAR, true);
    }

    protected PadKey getRightPadKey() {
        return new PadKey("Send", BUTTON_SEND, true);
    }

    protected void onFunctionButtonClicked(String padValue, String keyValue) {
        switch (keyValue) {
            case BUTTON_CLEAR:
                clearAll();
            case BUTTON_SEND:
                //todo: handle your pin
                dismiss();
                break;
        }
    }

    public static class PadKey {

        public final String padValue;
        public final String keyValue;
        public final boolean isFunctional;

        public PadKey(String padValue, String keyValue, boolean isFunctional) {
            this.padValue = padValue;
            this.keyValue = keyValue;
            this.isFunctional = isFunctional;
        }

        @NonNull
        @Override
        public String toString() {
            return padValue;
        }
    }

    private static class VH extends RecyclerView.ViewHolder {

        private Button mButton;

        private VH(@NonNull View itemView) {
            super(itemView);
            mButton = itemView.findViewById(R.id.mButton);
        }

        private Button getButton() {
            return mButton;
        }

    }

    public void clearAll() {
        Log.d("Button", "Clear");
        for(int x = 0; x < chars.length; x++) {
            chars[x].setText("");
        }
    }

    public void appendOneChar(String in) {
        Log.d("Button", in);
        for(int x = 0; x < chars.length; x++) {
            if(chars[x].getText().toString().isEmpty()) {
                chars[x].setText(in);
                x = chars.length + 1;
            }
        }
    }

}
