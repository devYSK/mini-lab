package com.yscorp.example1

import org.apache.kafka.common.serialization.Serializer

class CustomSerializer : Serializer<Customer> {
    override fun serialize(topic: String?, data: Customer?): ByteArray {
        return data.toString().toByteArray()
    }
}

class Customer(
    val id : Int,
    val name : String,
)
