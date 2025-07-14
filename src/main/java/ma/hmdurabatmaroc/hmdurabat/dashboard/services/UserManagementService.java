package ma.hmdurabatmaroc.hmdurabat.dashboard.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ma.hmdurabatmaroc.hmdurabat.dashboard.dto.UserDTO;
import ma.hmdurabatmaroc.hmdurabat.dashboard.dto.UserResponseDTO;
import ma.hmdurabatmaroc.hmdurabat.security.entities.User;
import ma.hmdurabatmaroc.hmdurabat.security.entities.Role;
import ma.hmdurabatmaroc.hmdurabat.security.repositories.UserRepository;
import ma.hmdurabatmaroc.hmdurabat.security.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String createUser(UserDTO userDTO) {
        Role role;
        if (userDTO.getRoleId() != null) {
            role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role ID not found"));
        } else {
            role = roleRepository.findByRoleName(userDTO.getRole())
                .orElseThrow(() -> new RuntimeException("Role name not found"));
        }
        
        return createUserWithRole(userDTO.getUsername(), userDTO.getPassword(), role);
    }

    @Transactional
    public String createAdminUser(String username, String password, Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role ID " + roleId + " not found"));
            
        return createUserWithRole(username, password, role);
    }

    private String createUserWithRole(String username, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singleton(role));
        user.setActive(true);
        
        userRepository.save(user);
        return "User created successfully";
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> {
                UserResponseDTO dto = new UserResponseDTO();
                dto.setId(user.getId());
                dto.setUsername(user.getUsername());
                // Get all role names
                List<String> roleNames = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());
                dto.setRoles(roleNames);
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public String deleteUser(Long id) {
        return userRepository.findById(id)
            .map(user -> {
                userRepository.delete(user);
                return "User deleted successfully";
            })
            .orElse("User not found");
    }
    
    @Transactional
    public String updateUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("At least one role must be provided");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.size() != roleIds.size()) {
            List<Long> foundIds = roles.stream().map(Role::getId).collect(Collectors.toList());
            List<Long> missingIds = roleIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
            throw new RuntimeException("Roles not found with IDs: " + missingIds);
        }

        user.setRoles(new java.util.HashSet<>(roles));
        userRepository.save(user);

        return "User roles updated successfully";
    }
}
