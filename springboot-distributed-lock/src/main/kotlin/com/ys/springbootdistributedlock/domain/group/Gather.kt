package com.ys.springbootdistributedlock.domain.group

import com.ys.springbootdistributedlock.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "gathers")
class Gather(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "gather", cascade = [CascadeType.ALL])
    val members: MutableList<User>,

    var currentMemberCount: Int = 0,

    val limitsCount: Int,

    ) {

    constructor(limitsCount: Int, user: User) : this(null, mutableListOf(user), 1, limitsCount)

    fun join(user: User) {
        require(currentMemberCount < limitsCount) { "모든 회원이 찼습니다" }

        println("$currentMemberCount, $limitsCount")

        if (!this.members.contains(user)) {
            this.members.add(user)
            user.join(this)
            println("join + ${user.id}")
            this.currentMemberCount += 1
            println("currentMemberCount : $currentMemberCount")
        }
    }

}