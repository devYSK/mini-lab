package com.ys.springbootdistributedlock.domain.group

import com.ys.springbootnamedlockjpa.domain.group.Gather
import org.springframework.data.jpa.repository.JpaRepository

interface GatherRepository : JpaRepository<Gather, Long> {
}