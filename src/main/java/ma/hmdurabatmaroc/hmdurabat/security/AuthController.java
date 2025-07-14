package ma.hmdurabatmaroc.hmdurabat.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import ma.hmdurabatmaroc.hmdurabat.security.dto.AuthRequest;
import ma.hmdurabatmaroc.hmdurabat.security.dto.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ma.hmdurabatmaroc.hmdurabat.security.entities.User;
import ma.hmdurabatmaroc.hmdurabat.security.repositories.RoleRepository;
import ma.hmdurabatmaroc.hmdurabat.security.repositories.UserRepository;
import ma.hmdurabatmaroc.hmdurabat.security.entities.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                         JwtTokenUtil jwtTokenUtil,
                         UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
            )
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        // Debug logging
        System.out.println("User logged in: " + userDetails.getUsername());
        System.out.println("Roles: " + userDetails.getAuthorities());
        System.out.println("Generated token: " + token);

        return new AuthResponse(token, userDetails.getUsername());
    }
}
