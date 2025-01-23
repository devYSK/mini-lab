package com.ys.util.valid

import jakarta.validation.constraints.NotNull


data class CreateMemberMoneyCommand(
    @NotNull private val membershipId: String,
) : SelfValidating<CreateMemberMoneyCommand>() {

    init {
        validateSelf()
    }

}

