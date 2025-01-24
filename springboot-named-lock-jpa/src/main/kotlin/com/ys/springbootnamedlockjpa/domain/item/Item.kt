package com.ys.springbootnamedlockjpa.domain.item

import jakarta.persistence.*

@Entity
@Table(name = "items")
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