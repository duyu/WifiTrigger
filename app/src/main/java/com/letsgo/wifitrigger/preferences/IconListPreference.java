package com.letsgo.wifitrigger.preferences;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.letsgo.wifitrigger.R;

/**
 * Created by anders on 14-11-18.
 */
public class IconListPreference extends ListPreference {

    private int[] mEntryIcons = null;

    private int mClickedDialogEntryIndex;

    public IconListPreference(Context context) {
        this(context, null);
    }

    public IconListPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconListPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_icon);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconPreference, defStyle, 0);

        int entryIconsResId = a.getResourceId(R.styleable.IconPreference_entryIcons, -1);
        if (entryIconsResId != -1) {
            setEntryIcons(entryIconsResId);
        }

        a.recycle();

    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        if (imageView != null) {
            imageView.setImageDrawable(view.getResources().getDrawable(getEntryIcon()));
        }
    }

    public void setEntryIcons(int[] entryIcons) {
        mEntryIcons = entryIcons;
    }

    public void setEntryIcons(int entryIconsResId) {
        TypedArray icons_array = getContext().getResources().obtainTypedArray(entryIconsResId);
        int[] icon_ids_array = new int[icons_array.length()];
        for (int i = 0; i < icons_array.length(); i++) {
            icon_ids_array[i] = icons_array.getResourceId(i, -1);
        }
        setEntryIcons(icon_ids_array);
        icons_array.recycle();
    }

    private int getValueIndex() {
        return findIndexOfValue(getValue());
    }

    public int getEntryIcon() {
        int index = getValueIndex();
        if (index >= 0 && mEntryIcons != null)
            return mEntryIcons[index];
        else
            return -1;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        if (mEntryIcons != null && getEntries().length != mEntryIcons.length) {
            throw new IllegalStateException("IconListPreference requires the icons entries array be the same length than entries or null");
        }

        mClickedDialogEntryIndex = getValueIndex();
        builder.setAdapter(new IconListPreferenceScreenAdapter(getContext()), null);

    }
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    private class IconListPreferenceScreenAdapter extends BaseAdapter implements ListAdapter {
        Context mContext;

        public IconListPreferenceScreenAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            return getEntries().length;
        }

        class CustomHolder {
            private  ImageView icon = null;
            private TextView text = null;
            private RadioButton rButton = null;
            private int value = -1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CustomHolder holder;

            if (convertView == null) {
                holder = new CustomHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.image_list_preference_row, parent, false);
                holder.icon = (ImageView) convertView.findViewById(R.id.image_list_view_row_icon);
                holder.text = (TextView) convertView.findViewById(R.id.image_list_view_row_text_view);
                holder.rButton = (RadioButton) convertView.findViewById(R.id.image_list_view_row_radio_button);
                convertView.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        view.requestFocus();

                        mClickedDialogEntryIndex = ((CustomHolder) view.getTag()).value;
                        IconListPreference.this.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                        getDialog().dismiss();
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (CustomHolder) convertView.getTag();
            }

            holder.icon.setImageResource(mEntryIcons[position]);
            holder.text.setText(getEntries()[position]);
            holder.rButton.setChecked(getValueIndex() == position);
            holder.value = position;

            return convertView;
        }

    }
}