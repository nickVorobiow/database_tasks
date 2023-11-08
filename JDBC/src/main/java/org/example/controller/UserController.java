package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.User;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.findAllUsers();
    }

    @GetMapping("/params")
    public List<User> getUsersForParams(@RequestParam(value = "nameFilter", defaultValue = "") String nameFilter,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {


        return userService.getUsersForParams(from, size, nameFilter);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
    }
}
