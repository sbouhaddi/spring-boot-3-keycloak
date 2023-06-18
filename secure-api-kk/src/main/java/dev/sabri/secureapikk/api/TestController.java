package dev.sabri.secureapikk.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/anonymous")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getAnonymous() {
        return ResponseEntity.ok("Hello Anonymous");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> getAdmin(Authentication principal) {
        String userName = principal.getName();
        return ResponseEntity.ok("Hello User \nUser Name : " + userName);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<String> getUser(Authentication principal) {
        String userName = principal.getName();
        return ResponseEntity.ok("Hello User \nUser Name : " + userName);
    }
}
