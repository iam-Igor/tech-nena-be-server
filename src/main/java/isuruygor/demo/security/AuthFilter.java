package isuruygor.demo.security;

import io.jsonwebtoken.ExpiredJwtException;
import isuruygor.demo.entities.User;
import isuruygor.demo.exceptions.NotFoundException;
import isuruygor.demo.exceptions.UnauthorizedException;
import isuruygor.demo.repositories.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.stream.Stream;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Autowired
    private JWTTools jwtTools;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("Inserisci il token nell'Authorization header");
            } else {
                String accessToken = authHeader.substring(7);

                // verifica del token
                jwtTools.verifyToken(accessToken);


                String id = jwtTools.extractIdFromToken(accessToken);
                User user = userRepo.findById(Long.parseLong(id)).orElseThrow(() -> new NotFoundException(id));

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);

            }
        } catch (ExpiredJwtException | NotFoundException | UnauthorizedException ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] allowedPaths = {"/auth/**", "/posts/categories", "/posts/approved"};

        return Stream.of(allowedPaths)
                .anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
    }
}
