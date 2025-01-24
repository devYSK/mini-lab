package com.ys.springbootnamedlockjpa.domain.item

import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepository : JpaRepository<Item, Long> {

}