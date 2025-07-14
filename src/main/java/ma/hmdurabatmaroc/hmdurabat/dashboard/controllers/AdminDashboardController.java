package ma.hmdurabatmaroc.hmdurabat.dashboard.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ma.hmdurabatmaroc.hmdurabat.dashboard.dto.UserDTO;
import ma.hmdurabatmaroc.hmdurabat.dashboard.dto.UserResponseDTO;
import ma.hmdurabatmaroc.hmdurabat.dashboard.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @GetMapping("/ping")
    public String ping(Authentication authentication) {
        System.out.println("Current user: " + authentication.getName());
        System.out.println("Current roles: " + authentication.getAuthorities());
        return "Admin dashboard is working! User: " + authentication.getName();
    }

    @Autowired
    private UserManagementService userManagementService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PostMapping("/create-admin-user")
    public String createAdminUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Bearer Token: " + (authHeader != null ? authHeader : "No token provided"));
        return userManagementService.createAdminUser(userDTO.getUsername(), userDTO.getPassword(), userDTO.getRoleId());
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        return userManagementService.deleteUser(id);
    }
    
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/users/{userId}/roles")
    public String updateUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return userManagementService.updateUserRoles(userId, roleIds);
    }
}
