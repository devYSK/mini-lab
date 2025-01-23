
https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html

테스트환경에서 주의할점
redis 에 저장하는 결과는 트랜잭션 롤백이 되지않음.

테스트내에서 @Transactional 을 사용해도 redis 에 저장되는 데이터들은 기본적으로 롤백되지 않습니다.
즉 테스트에서 저장한 데이터가 다른 테스트에 영향을 줄 수 있는 환경. (멱등성 지켜지지 않음.)
```kotlin
@BeforeEach
internal fun setUp() {
    flushRedis()
}

@AfterEach
internal fun tearDown() {
    redisTemplate.connectionFactory?.connection?.flushAll()
}

private fun flushRedis() {
    redisTemplate.connectionFactory?.connection?.flushAll()
}

```
이 부분을 해결하기 위해 테스트 전, 후로 flushAll() 을 호출해, redis 내의 데이터를 비워주시는것을 권장


[TIP] Redis cli 기본 명령어
타입확인
$ type {key}
저장
$ set {key} {value} # key, value 를 저장
$ mset {key} {value} [{key} {value} ...] # 여러 개의 key, value 를 한번에 저장
$ setex {key} {seconds} {value} # key, seconds, value 저장 (seconds 이후 휘발)
String 조회, 삭제
$ keys * # 현재 저장된 키값들을 모두 확인 (부하가 심한 명령어라 운영중인 서비스에선 절대 사용하면 안됨)
$ get {key} # 지정한 key 에 해당하는 value 를 가져옴
$ mget {key} [{key} ...] # 여러 개의 key 에 해당하는 value 를 한번에 가져옴
$ ttl {key} # key 의 만료 시간을 초 단위로 보여줌 (-1 은 만료시간 없음, -2 는 데이터 없음)
$ pttl {key} # key 의 만료 시간을 밀리초 단위로 보여줌
$ type {key} # 해당 key 의 value 타입 확인
$ del {key} [{key} ...] # 해당 key 들을 삭제
Set 조회, 삭제
$ smembers {key}
$ srem {key} {member [{member} ...]}
Hash 조회, 삭제
$ hkeys {key} # 필드 조회
$ hget {key} {field}
$ hdel {key} {field} [{field} ...]
전체 키 삭제
$ flushall
