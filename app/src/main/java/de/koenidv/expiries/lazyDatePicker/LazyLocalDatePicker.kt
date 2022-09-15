package de.koenidv.expiries.lazyDatePicker

import android.content.Context
import android.util.AttributeSet
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * Copyright (C) 2020 Mikhael LOPEZ
 * Licensed under the Apache License Version 2.0
 */
class LazyLocalDatePicker  //region CONSTRUCTORS
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LazyDatePicker(context, attrs, defStyleAttr) {
    private var minDate: LocalDate? = null
    private var maxDate: LocalDate? = null
    private var onLocalDatePickListener: OnLocalDatePickListener? = null
    private var onLocalDateSelectedListener: OnLocalDateSelectedListener? = null

    //endregion
    //region PROTECTED METHOD
    override fun onDatePick() {
        if (onLocalDatePickListener != null) {
            onLocalDatePickListener!!.onLocalDatePick(localDate)
        }
    }

    override fun onDateSelected() {
        if (onLocalDateSelectedListener != null) {
            onLocalDateSelectedListener!!.onLocalDateSelected(localDate != null)
        }
    }

    override fun minDateIsNotNull(): Boolean {
        return minDate != null
    }

    override fun maxDateIsNotNull(): Boolean {
        return maxDate != null
    }

    override fun checkMinDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToLocalDate(dateToCheckTmp.toString(), dateFormat?.value)
        return realDateToCheckTmp.isBefore(minDate)
    }

    override fun checkMaxDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToLocalDate(dateToCheckTmp.toString(), dateFormat?.value)
        return realDateToCheckTmp.isAfter(maxDate)
    }

    override fun checkSameDate(dateToCheckTmp: StringBuilder): Boolean {
        val realDateToCheckTmp = stringToLocalDate(dateToCheckTmp.toString(), dateFormat?.value)
        return dateToString(realDateToCheckTmp, dateFormat?.value) == dateToCheckTmp.toString()
    }

    //endregion
    //region PUBLIC METHOD
    var localDate: LocalDate?
        get() = if (date?.length == LENGTH_DATE_COMPLETE) {
            stringToLocalDate(date, dateFormat?.value)
        } else null
        set(newDate) {
            setLocalDate(newDate!!)
        }

    fun setLocalDate(newDate: LocalDate): Boolean {
        val tmpDate = dateToString(newDate, dateFormat?.value)
        if (tmpDate.length != LENGTH_DATE_COMPLETE || minDate != null && newDate.isBefore(minDate)
            || maxDate != null && newDate.isAfter(maxDate)
        ) {
            return false
        }
        date = tmpDate
        fillDate()
        return true
    }

    fun setMinLocalDate(minDate: LocalDate?) {
        this.minDate = minDate
        clear()
    }

    fun setMaxLocalDate(maxDate: LocalDate?) {
        this.maxDate = maxDate
        clear()
    }

    fun setOnLocalDatePickListener(onLocalDatePickListener: OnLocalDatePickListener?) {
        this.onLocalDatePickListener = onLocalDatePickListener
    }

    fun setOnLocalDateSelectedListener(onLocalDateSelectedListener: OnLocalDateSelectedListener?) {
        this.onLocalDateSelectedListener = onLocalDateSelectedListener
    }

    //endregion
    interface OnLocalDatePickListener {
        fun onLocalDatePick(dateSelected: LocalDate?)
    }

    interface OnLocalDateSelectedListener {
        fun onLocalDateSelected(dateSelected: Boolean?)
    }

    companion object {
        //endregion
        //region UTILS
        fun dateToString(date: LocalDate?, pattern: String?): String {
            return DateTimeFormatter.ofPattern(pattern).format(date)
        }

        fun stringToLocalDate(date: String?, format: String?): LocalDate {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(format))
        }
    }
}