package exchange.kanga.domain.learn.library.response

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity


class FailureResponse(val text:String): ResponseEntity<String>(HttpStatus.BAD_REQUEST)


