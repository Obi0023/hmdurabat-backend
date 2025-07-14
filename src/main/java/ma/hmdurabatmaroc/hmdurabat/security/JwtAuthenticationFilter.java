package ma.hmdurabatmaroc.hmdurabat.security;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("[DEBUG] Request path: " + path);

        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("[DEBUG] Authorization header: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("[DEBUG] Extracted JWT: " + jwt);

            username = jwtTokenUtil.extractUsername(jwt);
            System.out.println("[DEBUG] Extracted username from JWT: " + username);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            System.out.println("[DEBUG] Loaded UserDetails: " + userDetails.getUsername());

            boolean isValid = jwtTokenUtil.validateToken(jwt, userDetails);
            System.out.println("[DEBUG] JWT valid: " + isValid);

            if (isValid) {
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities());
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("[DEBUG] Authentication set in security context with roles: " + 
                    userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.joining(", ")));
            }
        } else {
            System.out.println("[DEBUG] No username found or authentication already set");
        }

        filterChain.doFilter(request, response);
    }

}
