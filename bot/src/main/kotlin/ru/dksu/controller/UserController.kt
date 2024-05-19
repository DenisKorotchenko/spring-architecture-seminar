//package ru.dksu.controller
//
//import org.springframework.web.bind.annotation.DeleteMapping
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.PutMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import ru.dksu.dao.UserDao
//import ru.dksu.db.entity.UserEntity
//
//@RestController
//@RequestMapping("/user")
//class UserController(
//    val userDao: UserDao
//) {
//    data class UserDto(val id: Int, val name: String, val age: Int)
//
//    @GetMapping("{id}")
//    fun getById(@PathVariable id: Int): UserDto {
//        return userDao.findById(id).let {
//            UserDto(
//                it.id,
//                it.name,
//                it.age
//            )
//        }
//    }
//
//    @GetMapping
//    fun getAll(): List<UserDto> {
//        return userDao.findAll().map {
//            UserDto(it.id, it.name, it.age)
//        }
//    }
//
//    data class InsertUserDto(val name: String, val age: Int)
//
//    @PutMapping
//    fun insert(@RequestBody userDto: InsertUserDto) {
//        userDao.insert(
//            UserEntity(
//            userDto.name, userDto.age)
//        )
//    }
//
//    @DeleteMapping("{id}")
//    fun delete(@PathVariable id: Int) {
//        return userDao.delete(id)
//    }
//}