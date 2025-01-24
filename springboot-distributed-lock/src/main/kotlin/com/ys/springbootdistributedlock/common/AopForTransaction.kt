package com.ys.springbootdistributedlock.common

import org.aspectj.lang.ProceedingJoinPoint
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * AOP에서 트랜잭션 분리를 위한 클래스
 */
@Component
class AopForTransaction {


    /**
     * @DistributedLock 이 선언된 메서드는
     * Propagation.REQUIRES_NEW 옵션을 지정해 부모 트랜잭션의 유무에 관계없이 별도의 트랜잭션으로 동작하게끔 처리해야합니다.
     * 락의 해제가 트랜잭션 커밋보다 먼저 이뤄지면 데이터 정합성이 깨질 수 있으므로
     * 반드시 트랜잭션 커밋 이후 락이 해제되어야합니다.
     * 동시성 환경에서 데이터의 정합성을 보장하기 위해 트랜잭션 커밋 이후 락이 해제되어야 합니다..
     */
    // https://helloworld.kurly.com/blog/distributed-redisson-lock/
    // joinPoint는 void 인경우 null 반환
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Throws(Throwable::class)
    fun proceed(joinPoint: ProceedingJoinPoint): Any? {
        return joinPoint.proceed()
    }


}