package com.ys.springbootnamedlockjpa

import com.ys.springbootnamedlockjpa.infra.NamedLockWithJdbcTemplate

//@Slf4j
//@RequestMapping("/users")
//@RestController
//class UserController(
//    userService: UserService,
//    namedLockWithJdbcTemplate: NamedLockWithJdbcTemplate,
//    userLevelLockFinal: UserLevelLockFinal
//) {
//    private val userService: UserService
//    private val namedLockWithJdbcTemplate: NamedLockWithJdbcTemplate
//    private val userLevelLockFinal: UserLevelLockFinal
//
//    init {
//        this.userService = userService
//        this.namedLockWithJdbcTemplate = namedLockWithJdbcTemplate
//        this.userLevelLockFinal = userLevelLockFinal
//    }
//
//    /**
//     * USER LEVEL LOCK 사용 하지 않는다.
//     */
//    @PostMapping("/{userId}/add-new-card")
//    fun addNewCard(@PathVariable userId: Long?): Int {
//        return userService.addNewCard(userId)
//    }
//
//    /**
//     * JdbcTemplate 으로 구현한 버전 사용.
//     */
//    @PostMapping("/{userId}/add-new-card-with-template")
//    fun addNewCardWithTemplate(@PathVariable userId: Long): Int {
//        return namedLockWithJdbcTemplate.executeWithLock(
//            userId.toString(),
//            LOCK_TIMEOUT_SECONDS
//        ) { userService.addNewCard(userId) }
//    }
//
//    /**
//     * 최종 버전 사용.
//     */
//    @PostMapping("/{userId}/add-new-card-final")
//    fun addNewCardFinal(@PathVariable userId: Long): Int {
//        return userLevelLockFinal.executeWithLock(
//            userId.toString(),
//            LOCK_TIMEOUT_SECONDS
//        ) { userService.addNewCard(userId) }
//    }
//
//    companion object {
//        const val ADD_CARD_URI = "http://localhost:8080/users/{userId}/add-new-card"
//        const val ADD_CARD_URI_WITH_TEMPLATE = "http://localhost:8080/users/{userId}/add-new-card-with-template"
//        const val ADD_CARD_URI_FINAL = "http://localhost:8080/users/{userId}/add-new-card-final"
//        private const val LOCK_TIMEOUT_SECONDS = 3
//    }
//}