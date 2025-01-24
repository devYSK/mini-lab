package com.ys.springbootdistributedlock.domain.group

import com.ys.springbootnamedlockjpa.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GatherService(
    private val gatherRepository: GatherRepository,
    private val userRepository: UserRepository,

    ) {

    @Transactional
    fun join(groupId: Long, userId: Long) {
        val gather = gatherRepository.findById(groupId).orElseThrow()
        val user = userRepository.findById(userId).orElseThrow()

        gather.join(user)
    }

}