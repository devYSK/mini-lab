package com.ys.util.valid

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validation
import jakarta.validation.Validator


abstract class SelfValidating<T> protected constructor() {
    private val validator: Validator

    init {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    /**
     * Evaluates all Bean Validations on the attributes of this
     * instance.
     */
    protected fun validateSelf() {
        val violations = validator.validate(this as T)
        if (violations.isNotEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

}

