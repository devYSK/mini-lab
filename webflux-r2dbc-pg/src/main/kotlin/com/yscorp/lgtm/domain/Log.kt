package com.yscorp.lgtm.domain

import com.github.f4b6a3.tsid.TsidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("logs")
class Log : Persistable<String> {

    @Id
    @get:JvmName("id")
    val id: String = TsidCreator.getTsid().toString()

    override fun getId(): String? {
        return id
    }

    override fun isNew(): Boolean {
        return this.id.isNotBlank()
    }
}
