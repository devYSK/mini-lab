package com.ys.springbootnamedlockjpa.infra

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import javax.sql.DataSource

@Repository
class NamedLockWithDataSource(
    private val dataSource: DataSource
) {

    fun <T> executeWithLock(
        userLockName: String,
        timeoutSeconds: Int,
        action: () -> T
    ): T = dataSource.connection.use { connection ->
        getLock(connection, userLockName, timeoutSeconds)

        val result = action()

        releaseLock(connection, userLockName)

        return result
    }

    private fun getLock(
        connection: Connection,
        userLockName: String,
        timeoutSeconds: Int
    ) = connection.prepareStatement(GET_LOCK).use { preparedStatement ->

        preparedStatement.setString(1, userLockName)
        preparedStatement.setInt(2, timeoutSeconds)

        checkResultSet(userLockName, preparedStatement, LockFunction.GET_LOCK)
    }

    private fun releaseLock(
        connection: Connection,
        userLockName: String
    ) = connection.prepareStatement(RELEASE_LOCK).use { preparedStatement ->

        preparedStatement.setString(1, userLockName)

        checkResultSet(userLockName, preparedStatement, LockFunction.RELEASE_LOCK)
    }

    private fun checkResultSet(
        lockName: String,
        preparedStatement: PreparedStatement,
        type: LockFunction
    ) = preparedStatement.executeQuery().use { resultSet ->

        if (!resultSet.next()) {
            log.error("Named Lock 결과 값이 없습니다. type = ${type.name}, lockName $lockName")

            throw RuntimeException(EXCEPTION_MESSAGE)
        }

        val result = resultSet.getInt(1)

        if (result != 1) {
            log.error("Named Lock 획득 실패  type = ${type.name}, lockName $lockName")

            throw RuntimeException(EXCEPTION_MESSAGE)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(NamedLockWithDataSource::class.java)
        private const val GET_LOCK = "SELECT GET_LOCK(?, ?)"
        private const val RELEASE_LOCK = "SELECT RELEASE_LOCK(?)"
        private const val EXCEPTION_MESSAGE = "NAMED LOCK 을 수행하는 중에 오류가 발생하였습니다."
    }

    enum class LockFunction {
        GET_LOCK,
        RELEASE_LOCK
    }

}