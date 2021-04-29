package name.pali.scorepion.redirect;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class RedirectController {

    @GetMapping
    public ResponseEntity<Void> redirect(){
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment("swagger-ui.html").build().toUri();
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}