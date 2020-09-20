package com.akusuka.githubers.utils

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.akusuka.githubers.R


class TimePreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.preferenceStyle,
    defStyleRes: Int = defStyleAttr
) : DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    private var mTime = "00:00"
    private val mDialogLayoutResId: Int = R.layout.preference_dialog_time

    var time: String
        get() = mTime
        set(time) {
            mTime = time
            // Save to SharedPreference
            persistString(time)
        }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        time = getPersistedString(defaultValue as? String)
    }

}
