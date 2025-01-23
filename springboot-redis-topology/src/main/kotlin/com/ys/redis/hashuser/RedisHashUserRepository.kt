package com.ys.redis.hashuser

import org.springframework.data.repository.CrudRepository


interface RedisHashUserRepository : CrudRepository<RedisHashUser, Long>

