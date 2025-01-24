package com.ys.springbootdistributedlock.domain.group

import org.springframework.data.jpa.repository.JpaRepository

interface GatherRepository : JpaRepository<Gather, Long> {
}