package com.nodev.ssvalidator

import org.apache.commons.validator.routines.DateValidator
import org.apache.commons.validator.routines.EmailValidator

class Validator {

    var string: String = ""
    var tag: String = ValidationConstant.DEFAULT_TAG
    var validationType = ArrayList<String>()

    constructor(string: String, tag: String = ValidationConstant.DEFAULT_TAG) {
        this.string = string
        this.tag = tag
    }

    constructor()

    private val singleResult = ArrayList<ValidatorData>()

    fun validateBulk(vararg list: Validator): List<ValidatorData> {
        val valdationResult = ArrayList<ValidatorData>()
        list.forEach {
            valdationResult.add(ValidatorData(it.validate(), it.tag))
        }
        return valdationResult
    }

    fun required(): Validator {
        registerResult(!string.isEmpty(), ValidationConstant.NOTNULL_TYPE)
        return this
    }

    fun isNumber(): Validator {
        registerResult(string.matches("^[0-9]*\$".toRegex()), ValidationConstant.NUMBER_TYPE)
        return this
    }

    fun isDate(pattern: String): Validator {
        registerResult(DateValidator.getInstance().isValid(string, pattern), ValidationConstant.DATE_TYPE)
        return this
    }

    fun length(min: Int, max: Int? = null): Validator {
        val sLength = string.length
        if (max == null) {
            registerResult(sLength == min, "${ValidationConstant.LENGTH_TYPE} $min")
        } else {
            registerResult(sLength in min..max, "${ValidationConstant.LENGTH_TYPE} between $min and $max")
        }
        return this
    }

    fun isEmail(): Validator {
        registerResult(EmailValidator.getInstance().isValid(string), ValidationConstant.EMAIL_TYPE)
        return this
    }

    fun regex(regex: String): Validator {
        registerResult(string.matches(regex.toRegex()), ValidationConstant.REGEX_TYPE)
        return this
    }

    fun alphaNumeric(): Validator {
        registerResult(
            string.matches("^([0-9]+[a-zA-Z]+|[a-zA-Z]+[0-9]+)[0-9a-zA-Z]*\$".toRegex()),
            ValidationConstant.ALPHANUMERIC_TYPE
        )
        return this
    }

    fun equalTo(compareTo: String): Validator {
        registerResult(
            compareTo == string,
            ValidationConstant.COMPARE_STRING_TYPE
        )
        return this
    }

    fun isAlphabet(): Validator {
        registerResult(string.matches("^[a-zA-Z]\$".toRegex()), ValidationConstant.ALPHABET_TYPE)
        return this
    }

    private fun registerResult(validationResult: Boolean, type: String) {
        validationType.add(type)

        if (validationResult) {
            singleResult.add(ValidatorData(true, tag))
        } else {
            singleResult.add(
                ValidatorData(
                    false,
                    tag
                )
            )
        }
    }

    fun validate(): Boolean {
        return if (singleResult.isEmpty()) {
            true
        } else {
            var value = true
            singleResult.forEach {
                if (!it.result) {
                    value = false
                }
            }
            value
        }
    }

    fun validateWithType(): ValidatorDataResult {
        return if (singleResult.isEmpty()) {
            ValidatorDataResult(true, ValidationConstant.DEFAULT_TAG, ValidationConstant.UNDEFINED_TYPE)
        } else {
            var value = true
            singleResult.forEach {
                if (!it.result) {
                    value = false
                }
            }
            ValidatorDataResult(value, tag, validationType.joinToString(separator = ","))
        }
    }

}