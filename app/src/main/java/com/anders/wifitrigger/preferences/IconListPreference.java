package com.anders.wifitrigger.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.anders.wifitrigger.R;

/**
 * Created by anders on 14-11-18.
 */
public class IconListPreference extends ListPreference {

    private int[] mEntryIcons = null;

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
        return index >= 0 && mEntryIcons != null ? mEntryIcons[index] : null;
    }


}