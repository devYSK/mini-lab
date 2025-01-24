package com.ys.springbootdistributedlock.domain.user

import com.ys.springbootdistributedlock.domain.group.Gather
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    val name: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "group_id")
    var gather: Gather?

) {

    fun join(gather: Gather) {
        this.gather = gather
    }

    companion object {
        fun create(name: String): User {
            return User(null, name, null)
        }
    }

}