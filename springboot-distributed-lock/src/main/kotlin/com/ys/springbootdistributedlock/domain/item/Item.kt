package com.ys.springbootdistributedlock.domain.item

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Item protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    var stockQuantity: Long,

    var name: String
) {

    constructor(stock: Long, name: String) : this(null, stock, name) {
        require(stock > 0) { "재고는 0 미만 불가능" }
    }

    fun decrease(quantity: Long) {
        require(this.stockQuantity - quantity >= 0) { "재고 부족" }

        this.stockQuantity -= quantity
    }

}