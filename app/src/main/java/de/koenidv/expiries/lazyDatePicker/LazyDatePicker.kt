package de.koenidv.expiries.lazyDatePicker

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.CycleInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import de.koenidv.expiries.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2020 Mikhael LOPEZ
 * Licensed under the Apache License Version 2.0
 */
open class LazyDatePicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private lateinit var editLazyDatePickerReal: EditText
    private lateinit var layoutLazyDatePicker: LinearLayout
    private lateinit var textLazyDatePickerDate: TextView
    private lateinit var textLazyDate1: TextView
    private lateinit var viewLazyDate1: View
    private lateinit var textLazyDate2: TextView
    private lateinit var viewLazyDate2: View
    private lateinit var textLazyDate3: TextView
    private lateinit var viewLazyDate3: View
    private lateinit var textLazyDate4: TextView
    private lateinit var viewLazyDate4: View
    private lateinit var textLazyDate5: TextView
    private lateinit var viewLazyDate5: View
    private lateinit var textLazyDate6: TextView
    private lateinit var viewLazyDate6: View
    private lateinit var textLazyDate7: TextView
    private lateinit var viewLazyDate7: View
    private lateinit var textLazyDate8: TextView
    private lateinit var viewLazyDate8: View

    // Properties
    protected var date: String? = null
    private var textColor = 0
    private var hintColor = 0
    private var minDate: Date? = null
    private var maxDate: Date? = null
    protected var dateFormat: DateFormat? = null
    private var keyboardVisible = false
    private var shakeAnimationDoing = false
    private var showFullDate = true
    private var onDatePickListener: OnDatePickListener? = null
    private var onDateSelectedListener: OnDateSelectedListener? = null
    private var textWatcher: TextWatcher? = null

    //region CONSTRUCTORS
    init {
        init(context, attrs, defStyleAttr)
    }

    protected fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        inflate(context, R.layout.lazy_date_picker, this)

        // Load the styled attributes and set their properties
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.LazyDatePicker,
            defStyleAttr, 0
        )
        textColor = attributes.getColor(R.styleable.LazyDatePicker_ldp_text_color, Color.BLACK)
        hintColor = attributes.getColor(R.styleable.LazyDatePicker_ldp_hint_color, Color.GRAY)
        showFullDate = attributes.getBoolean(R.styleable.LazyDatePicker_ldp_show_full_date, true)
        val dateFormatValue = attributes.getInteger(
            R.styleable.LazyDatePicker_ldp_date_format,
            DateFormat.MM_DD_YYYY.attrValue
        )
        dateFormat = DateFormat.fromValue(dateFormatValue)
        attributes.recycle()
    }

    //endregion
    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) // onPause() called
        {
            hideKeyBoard(context)
        }
    }

    private fun hideKeyBoard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            if (showFullDate) {
                addKeyboardVisibilityListener(this, object : OnKeyboardVisibilityListener {
                    override fun onVisibilityChange(isVisible: Boolean) {
                        if (keyboardVisible != isVisible) {
                            keyboardVisible = isVisible
                            if (!keyboardVisible && editLazyDatePickerReal.isFocused) {
                                editLazyDatePickerReal.clearFocus()
                            }
                        }
                    }
                })
            }
            editLazyDatePickerReal.onFocusChangeListener =
                OnFocusChangeListener { _, hasFocus ->
                    showDate(date, hasFocus)
                    if (showFullDate) {
                        showFullDateLayout(hasFocus)
                    }
                    if (hasFocus) {
                        showKeyboard(editLazyDatePickerReal, context)
                    } else {
                        hideKeyBoard(context)
                    }
                }
            initTextWatcher()
        }
    }

    //region PRIVATE METHOD
    private fun initView() {
        date = ""
        editLazyDatePickerReal = findViewById(R.id.edit_lazy_date_picker_real)
        layoutLazyDatePicker = findViewById(R.id.layout_lazy_date_picker)
        textLazyDatePickerDate = findViewById(R.id.text_lazy_date_picker_date)
        textLazyDate1 = findViewById(R.id.text_lazy_date_1)
        viewLazyDate1 = findViewById(R.id.view_lazy_date_1)
        textLazyDate2 = findViewById(R.id.text_lazy_date_2)
        viewLazyDate2 = findViewById(R.id.view_lazy_date_2)
        textLazyDate3 = findViewById(R.id.text_lazy_date_3)
        viewLazyDate3 = findViewById(R.id.view_lazy_date_3)
        textLazyDate4 = findViewById(R.id.text_lazy_date_4)
        viewLazyDate4 = findViewById(R.id.view_lazy_date_4)
        textLazyDate5 = findViewById(R.id.text_lazy_date_5)
        viewLazyDate5 = findViewById(R.id.view_lazy_date_5)
        textLazyDate6 = findViewById(R.id.text_lazy_date_6)
        viewLazyDate6 = findViewById(R.id.view_lazy_date_6)
        textLazyDate7 = findViewById(R.id.text_lazy_date_7)
        viewLazyDate7 = findViewById(R.id.view_lazy_date_7)
        textLazyDate8 = findViewById(R.id.text_lazy_date_8)
        viewLazyDate8 = findViewById(R.id.view_lazy_date_8)
        textLazyDate1.setTextColor(hintColor)
        viewLazyDate1.setBackgroundColor(hintColor)
        viewLazyDate1.visibility = GONE
        textLazyDate2.setTextColor(hintColor)
        viewLazyDate2.setBackgroundColor(hintColor)
        viewLazyDate2.visibility = GONE
        textLazyDate3.setTextColor(hintColor)
        viewLazyDate3.setBackgroundColor(hintColor)
        viewLazyDate3.visibility = GONE
        textLazyDate4.setTextColor(hintColor)
        viewLazyDate4.setBackgroundColor(hintColor)
        viewLazyDate4.visibility = GONE
        textLazyDate5.setTextColor(hintColor)
        viewLazyDate5.setBackgroundColor(hintColor)
        viewLazyDate5.visibility = GONE
        textLazyDate6.setTextColor(hintColor)
        viewLazyDate6.setBackgroundColor(hintColor)
        viewLazyDate6.visibility = GONE
        textLazyDate7.setTextColor(hintColor)
        viewLazyDate7.setBackgroundColor(hintColor)
        viewLazyDate7.visibility = GONE
        textLazyDate8.setTextColor(hintColor)
        viewLazyDate8.setBackgroundColor(hintColor)
        viewLazyDate8.visibility = GONE
        if (dateFormat == DateFormat.MM_DD_YYYY) {
            textLazyDate1.text = context.getString(R.string.ldp_month)
            textLazyDate2.text = context.getString(R.string.ldp_month)
            textLazyDate3.text = context.getString(R.string.ldp_day)
            textLazyDate4.text = context.getString(R.string.ldp_day)
        } else if (dateFormat == DateFormat.DD_MM_YYYY) {
            textLazyDate1.text = context.getString(R.string.ldp_day)
            textLazyDate2.text = context.getString(R.string.ldp_day)
            textLazyDate3.text = context.getString(R.string.ldp_month)
            textLazyDate4.text = context.getString(R.string.ldp_month)
        }
        textLazyDate5.text = context.getString(R.string.ldp_year)
        textLazyDate6.text = context.getString(R.string.ldp_year)
        textLazyDate7.text = context.getString(R.string.ldp_year)
        textLazyDate8.text = context.getString(R.string.ldp_year)
        textLazyDatePickerDate.setTextColor(textColor)
        findViewById<View>(R.id.btn_lazy_date_picker_on_focus).setOnClickListener {
            editLazyDatePickerReal.isFocusableInTouchMode = true
            editLazyDatePickerReal.requestFocus()
            showKeyboard(editLazyDatePickerReal, context)
        }
    }

    private fun showFullDateLayout(hasFocus: Boolean) {
        if (!hasFocus && date!!.length == LENGTH_DATE_COMPLETE) {
            layoutLazyDatePicker.visibility = INVISIBLE
            textLazyDatePickerDate.visibility = VISIBLE
            textLazyDatePickerDate.text = dateToString(
                getDate(),
                dateFormat!!.completeFormatValue
            )
        } else if (layoutLazyDatePicker.visibility == INVISIBLE) {
            layoutLazyDatePicker.visibility = VISIBLE
            textLazyDatePickerDate.visibility = GONE
        }
    }

    private fun charIsValid(date: String?, unicodeChar: Char): Boolean {
        // Check if char is a digit
        if (!Character.isDigit(unicodeChar)) {
            return false
        }
        val length = date!!.length

        // Check Month & Day by dateFormat selected
        val value = Character.getNumericValue(unicodeChar)
        if (dateFormat == DateFormat.MM_DD_YYYY) {
            if (length == 0 && value > 1) { // M1
                return false
            } else if (length == 1 && (Character.getNumericValue(date[0]) == 1 && value > 2
                        || Character.getNumericValue(date[0]) == 0 && value == 0)
            ) { // M2
                return false
            } else if (length == 2 && value > 3) { // D1
                return false
            } else if (length == 3 && (Character.getNumericValue(date[2]) == 3 && value > 1
                        || Character.getNumericValue(date[2]) == 0 && value == 0)
            ) { // D2
                return false
            }
        } else if (dateFormat == DateFormat.DD_MM_YYYY) {
            if (length == 0 && value > 3) { // D1
                return false
            } else if (length == 1 && (Character.getNumericValue(date[0]) == 3 && value > 1
                        || Character.getNumericValue(date[0]) == 0 && value == 0)
            ) { // D2
                return false
            } else if (length == 2 && value > 1) { // M1
                return false
            } else if (length == 3 && (Character.getNumericValue(date[2]) == 1 && value > 2
                        || Character.getNumericValue(date[2]) == 0 && value == 0)
            ) { // M2
                return false
            }
        }

        // Check if date is between min & max date
        if (length > 3 && minDateIsNotNull()) {
            val dateToCheckTmp = StringBuilder(date + unicodeChar)
            while (dateToCheckTmp.length < LENGTH_DATE_COMPLETE) {
                dateToCheckTmp.append("9")
            }
            if (checkMinDate(dateToCheckTmp)) return false
        }
        if (length > 3 && maxDateIsNotNull()) {
            val dateToCheckTmp = StringBuilder(date + unicodeChar)
            while (dateToCheckTmp.length < LENGTH_DATE_COMPLETE) {
                dateToCheckTmp.append("0")
            }
            if (checkMaxDate(dateToCheckTmp)) return false
        }
        if (length > 6) {
            val dateToCheckTmp = StringBuilder(date + unicodeChar)
            while (dateToCheckTmp.length < LENGTH_DATE_COMPLETE) {
                dateToCheckTmp.append("9")
            }
            return checkSameDate(dateToCheckTmp)
        }
        return true
    }

    private fun showDate(value: String?, hasFocus: Boolean) {
        manageViewFocus(hasFocus, value!!.length)
        when (value.length) {
            0 -> {
                textLazyDate1.text =
                    context.getString(if (dateFormat == DateFormat.MM_DD_YYYY) R.string.ldp_month else R.string.ldp_day)
                textLazyDate1.setTextColor(hintColor)
            }
            1 -> {
                textLazyDate1.setTextColor(textColor)
                textLazyDate1.text = getLetterAt(0, value)
                textLazyDate2.text =
                    context.getString(if (dateFormat == DateFormat.MM_DD_YYYY) R.string.ldp_month else R.string.ldp_day)
                textLazyDate2.setTextColor(hintColor)
            }
            2 -> {
                textLazyDate2.setTextColor(textColor)
                textLazyDate2.text = getLetterAt(1, value)
                textLazyDate3.text =
                    context.getString(if (dateFormat == DateFormat.MM_DD_YYYY) R.string.ldp_day else R.string.ldp_month)
                textLazyDate3.setTextColor(hintColor)
            }
            3 -> {
                textLazyDate3.setTextColor(textColor)
                textLazyDate3.text = getLetterAt(2, value)
                textLazyDate4.text =
                    context.getString(if (dateFormat == DateFormat.MM_DD_YYYY) R.string.ldp_day else R.string.ldp_month)
                textLazyDate4.setTextColor(hintColor)
            }
            4 -> {
                textLazyDate4.setTextColor(textColor)
                textLazyDate4.text = getLetterAt(3, value)
                textLazyDate5.text = context.getString(R.string.ldp_year)
                textLazyDate5.setTextColor(hintColor)
            }
            5 -> {
                textLazyDate5.setTextColor(textColor)
                textLazyDate5.text = getLetterAt(4, value)
                textLazyDate6.text = context.getString(R.string.ldp_year)
                textLazyDate6.setTextColor(hintColor)
            }
            6 -> {
                textLazyDate6.setTextColor(textColor)
                textLazyDate6.text = getLetterAt(5, value)
                textLazyDate7.text = context.getString(R.string.ldp_year)
                textLazyDate7.setTextColor(hintColor)
            }
            7 -> {
                textLazyDate7.setTextColor(textColor)
                textLazyDate7.text = getLetterAt(6, value)
                textLazyDate8.text = context.getString(R.string.ldp_year)
                textLazyDate8.setTextColor(hintColor)
            }
            8 -> {
                textLazyDate8.setTextColor(textColor)
                textLazyDate8.text = getLetterAt(7, value)
            }
        }
    }

    private fun manageViewFocus(hasFocus: Boolean, valueLength: Int) {
        if (hasFocus) {
            viewLazyDate1.visibility = VISIBLE
            viewLazyDate2.visibility = VISIBLE
            viewLazyDate3.visibility = VISIBLE
            viewLazyDate4.visibility = VISIBLE
            viewLazyDate5.visibility = VISIBLE
            viewLazyDate6.visibility = VISIBLE
            viewLazyDate7.visibility = VISIBLE
            viewLazyDate8.visibility = VISIBLE
            when (valueLength) {
                0 -> {
                    viewLazyDate1.visibility = VISIBLE
                    viewLazyDate1.setBackgroundColor(textColor)
                    viewLazyDate2.setBackgroundColor(hintColor)
                }
                1 -> {
                    viewLazyDate1.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate2.visibility = VISIBLE
                    viewLazyDate2.setBackgroundColor(textColor)
                    viewLazyDate3.setBackgroundColor(hintColor)
                }
                2 -> {
                    viewLazyDate2.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate3.visibility = VISIBLE
                    viewLazyDate3.setBackgroundColor(textColor)
                    viewLazyDate4.setBackgroundColor(hintColor)
                }
                3 -> {
                    viewLazyDate3.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate4.visibility = VISIBLE
                    viewLazyDate4.setBackgroundColor(textColor)
                    viewLazyDate5.setBackgroundColor(hintColor)
                }
                4 -> {
                    viewLazyDate4.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate5.visibility = VISIBLE
                    viewLazyDate5.setBackgroundColor(textColor)
                    viewLazyDate6.setBackgroundColor(hintColor)
                }
                5 -> {
                    viewLazyDate5.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate6.visibility = VISIBLE
                    viewLazyDate6.setBackgroundColor(textColor)
                    viewLazyDate7.setBackgroundColor(hintColor)
                }
                6 -> {
                    viewLazyDate6.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate7.visibility = VISIBLE
                    viewLazyDate7.setBackgroundColor(textColor)
                    viewLazyDate8.setBackgroundColor(hintColor)
                }
                7 -> {
                    viewLazyDate7.setBackgroundColor(Color.TRANSPARENT)
                    viewLazyDate8.visibility = VISIBLE
                    viewLazyDate8.setBackgroundColor(textColor)
                }
                8 -> viewLazyDate8.setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            viewLazyDate1.visibility = GONE
            viewLazyDate2.visibility = GONE
            viewLazyDate3.visibility = GONE
            viewLazyDate4.visibility = GONE
            viewLazyDate5.visibility = GONE
            viewLazyDate6.visibility = GONE
            viewLazyDate7.visibility = GONE
            viewLazyDate8.visibility = GONE
        }
    }

    //endregion
    //region PROTECTED METHOD
    protected open fun onDatePick() {
        if (onDatePickListener != null) {
            onDatePickListener!!.onDatePick(getDate())
        }
    }

    protected open fun onDateSelected() {
        if (onDateSelectedListener != null) {
            onDateSelectedListener!!.onDateSelected(getDate() != null)
        }
    }

    protected open fun minDateIsNotNull(): Boolean {
        return minDate != null
    }

    protected open fun maxDateIsNotNull(): Boolean {
        return maxDate != null
    }

    protected open fun checkMinDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToDate(dateToCheckTmp.toString(), dateFormat!!.value)
        return realDateToCheckTmp == null || realDateToCheckTmp.before(minDate)
    }

    protected open fun checkMaxDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToDate(dateToCheckTmp.toString(), dateFormat!!.value)
        return realDateToCheckTmp == null || realDateToCheckTmp.after(maxDate)
    }

    protected open fun checkSameDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToDate(dateToCheckTmp.toString(), dateFormat!!.value)
        return dateToString(realDateToCheckTmp, dateFormat!!.value) == dateToCheckTmp.toString()
    }

    protected fun fillDate() {
        detachTextWatcher()
        editLazyDatePickerReal.setText(date)
        initTextWatcher()
        textLazyDate1.setTextColor(textColor)
        textLazyDate1.text = getLetterAt(0, date)
        textLazyDate2.setTextColor(textColor)
        textLazyDate2.text = getLetterAt(1, date)
        textLazyDate3.setTextColor(textColor)
        textLazyDate3.text = getLetterAt(2, date)
        textLazyDate4.setTextColor(textColor)
        textLazyDate4.text = getLetterAt(3, date)
        textLazyDate5.setTextColor(textColor)
        textLazyDate5.text = getLetterAt(4, date)
        textLazyDate6.setTextColor(textColor)
        textLazyDate6.text = getLetterAt(5, date)
        textLazyDate7.setTextColor(textColor)
        textLazyDate7.text = getLetterAt(6, date)
        textLazyDate8.setTextColor(textColor)
        textLazyDate8.text = getLetterAt(7, date)
        viewLazyDate1.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate2.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate3.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate4.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate5.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate6.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate7.setBackgroundColor(Color.TRANSPARENT)
        viewLazyDate8.setBackgroundColor(Color.TRANSPARENT)
        if (showFullDate) {
            showFullDateLayout(editLazyDatePickerReal.isFocused)
        }
    }

    //endregion
    //region PUBLIC METHOD
    fun focus() {
        editLazyDatePickerReal.isFocusableInTouchMode = true
        editLazyDatePickerReal.requestFocus()
    }

    fun getDate(): Date? {
        return if (date!!.length == LENGTH_DATE_COMPLETE) {
            stringToDate(date, dateFormat!!.value)
        } else null
    }

    fun setDate(newDate: Date): Boolean {
        val tmpDate = dateToString(newDate, dateFormat!!.value)
        if (tmpDate.length != LENGTH_DATE_COMPLETE || minDate != null && newDate.before(minDate)
            || maxDate != null && newDate.after(maxDate)
        ) {
            return false
        }
        date = tmpDate
        fillDate()
        return true
    }

    fun setMinDate(minDate: Date?) {
        this.minDate = minDate
        clear()
    }

    fun setMaxDate(maxDate: Date?) {
        this.maxDate = maxDate
        clear()
    }

    @JvmName("setPickerDateFormat")
    fun setDateFormat(dateFormat: DateFormat?) {
        this.dateFormat = dateFormat
        clear()
    }

    fun clear() {
        initView()
    }

    fun shake() {
        shakeView(layoutLazyDatePicker)
    }

    fun setOnDatePickListener(onDatePickListener: OnDatePickListener?) {
        this.onDatePickListener = onDatePickListener
    }

    fun setOnDateSelectedListener(onDateSelectedListener: OnDateSelectedListener?) {
        this.onDateSelectedListener = onDateSelectedListener
    }

    //endregion
    //region TEXT WATCHER
    private fun detachTextWatcher() {
        editLazyDatePickerReal.removeTextChangedListener(textWatcher)
        textWatcher = null
    }

    private fun initTextWatcher() {
        if (textWatcher == null) {
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (!shakeAnimationDoing) {
                        if (before > 0) {
                            // Remove last char
                            if (date!!.length > 0) {
                                date = date!!.substring(0, date!!.length - 1)
                            }
                        } else if (date!!.length < LENGTH_DATE_COMPLETE && s.length > 0 && charIsValid(
                                date,
                                s[s.length - 1]
                            )
                        ) {
                            val unicodeChar = s[s.length - 1]
                            date += unicodeChar
                            if (date!!.length == LENGTH_DATE_COMPLETE) {
                                onDatePick()
                            }
                        } else {
                            shakeView(layoutLazyDatePicker)
                        }
                        showDate(date, true)
                        onDateSelected()
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            }
            editLazyDatePickerReal.addTextChangedListener(textWatcher)
        }
    }

    //endregion
    //region KEYBOARD
    private fun addKeyboardVisibilityListener(
        rootLayout: View,
        onKeyboardVisibilityListener: OnKeyboardVisibilityListener
    ) {
        rootLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootLayout.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootLayout.rootView.height

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            val keypadHeight = screenHeight - r.bottom
            val isVisible = keypadHeight > screenHeight * 0.15 // 0.15 ratio is perhaps
            // enough to determine keypad height.
            onKeyboardVisibilityListener.onVisibilityChange(isVisible)
        }
    }

    private interface OnKeyboardVisibilityListener {
        fun onVisibilityChange(isVisible: Boolean)
    }

    private fun showKeyboard(editText: EditText?, context: Context) {
        editText!!.requestFocus()
        editText.setSelection(editText.length())
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    //endregion
    //region UTILS
    private fun getLetterAt(position: Int, value: String?): String {
        return value!![position].toString()
    }

    private fun shakeView(view: View?) {
        shakeAnimationDoing = true
        view!!.animate()
            .translationX(-15f).translationX(15f)
            .setDuration(30)
            .setInterpolator(CycleInterpolator(5f)) // 150 / 30
            .setDuration(150)
            .withEndAction { shakeAnimationDoing = false }
            .start()
    }

    //endregion
    interface OnDatePickListener {
        fun onDatePick(dateSelected: Date?)
    }

    interface OnDateSelectedListener {
        fun onDateSelected(dateSelected: Boolean?)
    }

    enum class DateFormat {
        MM_DD_YYYY, DD_MM_YYYY;

        val value: String
            get() {
                return when (this) {
                    MM_DD_YYYY -> "MMddyyyy"
                    DD_MM_YYYY -> "ddMMyyyy"
                }
            }
        val completeFormatValue: String
            get() {
                return when (this) {
                    MM_DD_YYYY -> "MMM dd yyyy"
                    DD_MM_YYYY -> "dd MMM yyyy"
                }
            }
        val attrValue: Int
            get() {
                return when (this) {
                    MM_DD_YYYY -> 1
                    DD_MM_YYYY -> 2
                }
            }

        companion object {
            fun fromValue(value: Int): DateFormat {
                when (value) {
                    1 -> return MM_DD_YYYY
                    2 -> return DD_MM_YYYY
                }
                throw IllegalArgumentException("This value is not supported for DateFormat: $value")
            }
        }
    }

    companion object {
        const val LENGTH_DATE_COMPLETE = 8
        fun dateToString(date: Date?, pattern: String?): String {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        }

        fun stringToDate(date: String?, format: String?): Date? {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }
    }
}