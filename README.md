# REST
REST API implementation using Spring MVC

### Spring annotation depending on version
| Http method | Spring 4.xx | Spring 5.xx |
| ----------- | ----------- | ------------|
| GET | @RequestMapping(value = "/url",  method = RequestMethod.GET) | @GetMapping("/url") |
| POST | @RequestMapping(value = "/url",  method = RequestMethod.POST) | @PostMapping("/url") |
| PUT | @RequestMapping(value = "/url",  method = RequestMethod.PUT) | @PutMapping("/url") |
| DELETE | @RequestMapping(value = "/url",  method = RequestMethod.DELETE) | @DeleteMapping("/url") |
| PATCH | @RequestMapping(value = "/url",  method = RequestMethod.PATCH) | @PatchMapping("/url") |

One more thing to memorize is if that it is required to produce media type it should be done like that

| Spring 4.xx | Spring 5.xx |
| ----------- | ------------|
| @RequestMapping(value = "/url",  method = RequestMethod.GET, produces = {"application/json", "application/xml"}) | @GetMapping("/url", produces = {"application/json", "application/xml"}) |

NOTE: `@RequestMapping` annotation by default is GET method by itself so that it may be written in this way `@RequestMapping("/url")`.

### @Controller and @RestController annotation
Before Spring 5.xx the way web declare controller to be restful was annotating it li regular controller `@Controller` and in each method was required to write `@ResponseBody`

#### Spring 4.xx 
```java
@Controller
@RequestMapping(value = "/v1/customers", produces = {"application/json", "application/xml"})
public class CustomerRESTController {
    @RequestMapping(value = "/{customerId}",  method = RequestMethod.GET)
    public @ResponseBody Customer getCustomerById(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomerById(customerId);
    }
}
```

Starting from Spring 5.xx annotation `@RestController` appeared which includes `@ResponseBody ` by default so that it is not required to write it.

#### Spring 5.xx
```java
@RestController
@RequestMapping(value = "/v1/customers", produces = {"application/json", "application/xml"})
public class CustomerRESTController {
    @GetMapping("/{customerId}")
    public Customer getCustomerById(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomerById(customerId);
    }
}
```

To document this API Swagger documentation auto-generation was used which is available by this url: [http://localhost:8080/rest/swagger-ui.html](http://localhost:8080/rest/swagger-ui.html)

#### This REST API uses oAuth2 to access all the endpoints
In this case all access token are stored into the database. To perform authorization client with secret and username with password are used.
But it is also able to do everything in-memory.

How to authorize:
1. Go to the following address - `http://localhost:8080/rest/oauth/token?grant_type=password&client_id=trusted-client&client_secret=trusted-client-secret&username=admin&password=123456` where `grant_type` is the way you are going to authorize, `client_id` is the client where the way of our authorization is declared `client_secret` `username` and `password` obviously are going to be user name and password
2. The next step is to have an access to the any API endpoint with the valid access token. As an example of accessing the list of all customers `http://localhost:8080/rest/api/v1/customers?access_token=token` and the value of `access_token` is going to be generated access_token from the response in step `1.`.